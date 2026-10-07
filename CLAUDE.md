# ApolloChunks

Серверный Fabric-мод для сервера Apollo-tech, расширение Carpet: чанки вокруг ботов Carpet грузятся раньше чанков живых игроков. Пользователь пишет по-русски.

## Сборка

`./gradlew build`. Minecraft 26.3 без обфускации: Loom `net.fabricmc.fabric-loom` 1.17-SNAPSHOT, без маппингов, имена Mojang. Gradle работает на системной Java 21, мод компилируется JDK 25 через toolchain. Loom 1.18 требует запускать сам Gradle на Java 25, поэтому стоит 1.17.
Carpet подключён как `implementation` с Modrinth maven, поэтому в dev-запуске он уже на classpath: класть его ещё и в `run/mods` нельзя, будет дубликат мода.
Ванильный код смотреть декомпиляцией jar'а `~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.3/` (Vineflower лежит в `~/.gradle/caches/modules-2/files-2.1/org.vineflower`).

## Как устроено

- `FakePlayerChunkLoader` — один на `DistanceManager` (то есть на измерение). Свой `ChunkTracker` считает расстояние Чебышёва до ближайшего чанка с ботом (как ванильный `PlayerTicketTracker`). Чанки в радиусе прорисовки получают тикет `apollochunks:fake_player` (уровень 31, `FLAG_LOADING`) через собственный троттлер на 4 слота, ближние первыми; слот освобождается, когда чанк стал entity ticking. Все чанки в радиусе прорисовки + `MAX_LEVEL - 31` получают приоритет очереди 0..30 по расстоянию до бота.
- Троттлер обязателен. Без него (тикеты на всю зону разом) сервер под нагрузкой подвисал на 2–5 с: `ServerChunkCache.hasChunk` считает чанк загруженным по уровню тикета, коллизии и жидкости на главном потоке синхронно ждут его генерацию, а впереди в очереди стояли сотни чанков бота. Найдено `jstack`'ом: `EntityFluidInteraction.hasFluidAndLoaded` и `Level.getChunkForCollisions` → `managedBlock`.
- `ChunkMapMixin` подменяет уровень в `ChunkMap.onLevelChange`: оттуда берут приоритет и очередь worldgen, и очередь света (`ChunkHolder.queueLevel`).
- Переприоритизация уже существующих `ChunkHolder` идёт только при входе в зону бота или выходе из неё, а не при каждом изменении расстояния: при движении бота иначе были бы тысячи пересортировок на каждый переход чанка.
- `TicketTypeMixin` регистрирует тип тикета в `<clinit>` `TicketType`: main-энтрипоинты Fabric без Fabric API вызываются после заморозки реестров (`Registry is already frozen`).
- `ServerLevelMixin` в конце тика вызывает `chunkSource.move` для ботов, сменивших секцию. Carpet сам делает это только на каждом 10-м тике (`EntityPlayerMPFake.tick`).

## Инварианты

- Ванильные тикеты `player_loading` не трогать: бот получает и ванильный (через троттлер), и наш тикет. Если наш трекер ошибётся, чанки бота всё равно догрузятся по-ванильному. Общий тикет того же типа делить нельзя: `TicketStorage` схлопывает тикеты одного типа и уровня, и ванилла сняла бы его из-под бота.
- Набор загружаемых чанков и их уровни должны совпадать с ванильными: радиус = серверный view distance (`updatePlayerTickets`), уровень 31. Меняются только порядок и скорость.
- Боты определяются как `instanceof EntityPlayerMPFake` (включая тени). Источник ботов — `DistanceManager.addPlayer/removePlayer`, поэтому всё, что исключает игрока из загрузки (спектаторы, `creativePlayersLoadChunks`, intricarpet), учитывается автоматически.
- Тикет без `FLAG_PERSIST`: не пишется в `chunk_tickets`, мод можно убрать без следов.

## Как проверять на dev-сервере

`run/` в `.gitignore`. `run/eula.txt` (`eula=true`), `run/server.properties`: `online-mode=false`, `enable-rcon=true`, `rcon.port=25598`, `rcon.password=test`, `server-port=25599`. `./gradlew runServer --args=nogui` в фоне, команды через маленький RCON-клиент на Python.
Тикеты смотреть scarpet'ом: `script run length(filter(chunk_tickets(), _:0 ~ 'flags=2'))`. Scarpet печатает тип тикета через `toString()` записи, поэтому `player_loading` и наш тикет выглядят одинаково (`TicketType[timeout=0, flags=2]`).
Для замеров скорости временно добавлялись: класс-проба, который логирует, через сколько мс все чанки в радиусе N вокруг бота стали entity ticking, и подмена `FakePlayers.isFake` → `false` для ботов с именем `Real*`, чтобы изображать живых игроков. Для `fakePlayerChunkUpdateEveryTick` проба считала тики, когда `getLastSectionPos()` бота отстаёт от его текущего чанка (бот-спектатор с `/player ... sprint` + `move forward`): с правилом 0 из 100, без него 30–40 из 100. В репо этих проб нет, после теста удалять.
Carpet подтягивает настоящие профили Mojang, поэтому имена ботов меняют регистр (`Real1` → `real1`, `REAL2`): сравнивать имена без учёта регистра.
