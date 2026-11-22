# Aether Client (gecici isim)

Minecraft 1.21.11, Fabric, Java 21, Mojang mappings. Telefon (Pojav/Zalith) icin hafif tutuldu.

## Telefondan derleme (PC gerekmez)

1. Bu klasoru bir GitHub reposuna yukle.
2. `gradle.properties` icindeki `fabric_version` degerini https://fabricmc.net/develop
   sayfasindan 1.21.11 icin kopyala. Gerekirse `build.gradle` icindeki Loom surumunu de.
3. Repo > Actions > build > Run workflow.
4. Bitince Artifacts bolumunden `aether-client` jar dosyasini indir.
5. Jar'i launcher'in `mods` klasorune at. Fabric Loader 0.20.1 ve Fabric API de kurulu olmali.

## Yapi

- `event/`   EventBus (yansimasiz, allocation'siz post)
- `setting/` Bool, Number, Enum ayarlari (JSON'a otomatik kaydolur)
- `module/`  Module tabani, ModuleManager, `impl/` altinda moduller
- `config/`  `config/aether.json`

Yeni modul: `module/impl/` altina sinif ekle, `ModuleManager.registerAll()` icine bir satir yaz.

## Sirada

Faz 2: komut sistemi (`.toggle Sprint`) ve dokunmatik ClickGUI.
