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
| `.packets [reset]` | Paket sayaclarini gosterir (once `PacketLogger` modulunu ac) |
| `.look <yaw> <pitch>` | Rotasyon yoneticisini test eder |
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
- `mixin/`   ConnectionMixin (paket hook'u, `require = 0`: hedef bulunamazsa oyun cokmez)
- `rotation/` Rotation (saf matematik) ve RotationManager (oncelikli, hiz sinirli)

Yeni modul: `module/impl/` altina sinif ekle, `ModuleManager.registerAll()` icine bir satir yaz.
Menu, komutlar ve config otomatik tanir.

## Faz 3 notlari

- **Paket hook'u dogrulanmadi:** `ConnectionMixin` 1.21.11 metot adlarini bellekten kullanir.
  Calismazsa oyun cokmez, `.packets` sifir gosterir. O zaman `logs/latest.log` icindeki Mixin
  uyarisini gelistiriciye ilet.
- **Thread uyarisi:** Paket olaylari netty thread'inde gelir. Dinleyiciler oyun nesnelerine
  dokunmadan once veriyi `TickEvent`'e tasimali (SetbackGuard bunu ornek alir).
- **SetbackGuard:** Ust uste geri cekilmede (varsayilan 5 sn'de 3) Movement ve Combat
  modullerini kapatir. `/tp` gibi normal isinlanmalar da sayilir.

## Sirada

Faz 4: render altyapisi ve render modulleri (ESP, Fullbright, Zoom, Nametags).
