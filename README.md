# Aether Client (gecici isim)

Minecraft 1.21.11, Fabric, Java 21, Mojang mappings. Telefon (Pojav/Zalith) icin hafif tutuldu.

## Telefondan derleme (PC gerekmez)

1. Repo > Actions > build > Run workflow.
2. Bitince Artifacts bolumunden `aether-client` jar dosyasini indir.
3. Jar'i launcher'in `mods` klasorune at. Fabric Loader 0.20.1+ ve Fabric API de kurulu olmali.

`gradle.properties` icindeki Fabric API ve `build.gradle` icindeki Loom surumunu
https://fabricmc.net/develop sayfasindan teyit et.

## Kullanim (oyun icinde)

Sohbeti ac ve yaz:

| Komut | Ne yapar |
|---|---|
| `.gui` | Dokunmatik menuyu acar |
| `.list` | Modulleri listeler (`+` = acik) |
| `.toggle <modul>` | Modulu acar/kapatir |
| `.set <modul> <ayar> [deger]` | Ayari gosterir/degistirir |
| `.save` | Config'i kaydeder |
| `.help` | Komut listesi |

Normal sohbette `.` ile baslayan mesaj gondermek icin `..` ile basla.

Menu: kategori sekmeleri, modul adina dokun = ac/kapat, `>` = ayarlar, sayi ayarlari icin `-`/`+`,
cok modul varsa Onceki/Sonraki.

## Yapi

- `event/`   EventBus (yansimasiz, allocation'siz post)
- `setting/` Bool, Number, Enum ayarlari (JSON'a otomatik kaydolur)
- `module/`  Module tabani, ModuleManager, `impl/` altinda moduller
- `command/` Sohbet komutlari
- `gui/`     Dokunmatik ClickGUI (sadece Button bilesenleri)
- `hud/`     Watermark ve aktif modul listesi
- `config/`  `config/aether.json`

Yeni modul: `module/impl/` altina sinif ekle, `ModuleManager.registerAll()` icine bir satir yaz.
Menu, komutlar ve config otomatik tanir.

## Sirada

Faz 3: paket olaylari, rotasyon yoneticisi, setback algilama (Mixin gerektirir).
