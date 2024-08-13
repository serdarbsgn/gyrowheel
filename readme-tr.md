# GyroWheel
For English documentation, please see [readme.md](readme.md).
## Kurulum

Python kurulumu ve teknik detaylarla uğraşmak istemiyorsanız, hazırladığım çalıştırılabilir dosyaları kullanabilirsiniz:

- [x64 çalıştırılabilir dosyasını indir](https://github.com/serdarbsgn/gyrowheel/raw/main/dist/GWListenerX64.exe)[Virustotal Linki](https://www.virustotal.com/gui/url/fe86b3f8f99be5c24d0e37fccf8e03e89ae39cb6e0b88d737b38541d0ef0ed9c)
- [x86 çalıştırılabilir dosyasını indir](https://github.com/serdarbsgn/gyrowheel/raw/main/dist/GWListenerX86.exe)[Virustotal Linki](https://www.virustotal.com/gui/url/64d2b243de5fdd899eb8f07c2ed794d40619f606673488ffe2d20d066ea94d8)

[ViGEm Bus Driver](https://vigembusdriver.com/download/)'ı da indirip yüklemeniz gerekecek. Bilgisayarınız için doğru mimarideki çalıştırılabilir dosyayı kullanın. (Birisi çalışmazsa, diğerini deneyin. ViGEm Bus Driver'ı yüklediğinizden emin olun.)

Çalıştırılabilir dosyaları kullanmak istemiyorsanız, aşağıya bakın.

![Run .exe](readme-photos/runexe.png "Run .exe")

Doğru çalıştırılabilir dosyayı kullanarak çalıştırma seçeneklerini seçin.

## Kurulum II

Python 3 yüklüyse, [Python'u buradan yükleyin](https://www.python.org/downloads/), ardından gerekli kütüphaneleri yüklemek için `install.bat` dosyasını kullanın. APK'yı telefonunuza yükleyin, ardından bilgisayarınızı ve telefonunuzu aynı ağa bağlayın. (Bluetooth modu için bu gerekli değildir.)

![Run .bat](readme-photos/run-bat.png "Run .bat")

Çalıştırma seçeneklerini seçmek için `run.bat` dosyasını kullanın.

## Ağ Modu Bağlantısı

![UDP Mode EXE](readme-photos/run-udp-mode-exe.png "UDP Mode EXE")

Ağ/UDP modunu kullanmak için, bilgisayarınızda görüntülenen IP'yi telefonunuza yazın. Örneğin, `192.168.1.42:12345`.

![UDP Mode](readme-photos/run-udp-mode.png "UDP Mode")

Ağ/UDP modunu kullanmak için, terminalde görüntülenen IP'yi telefonunuza yazın. Örneğin, `192.168.1.42:12345`.

![Enter IP on Phone](readme-photos/udp-mode-enter-ip.png "Enter IP on Phone")

"AĞ'I KULLAN" seçeneğini seçin ve `192.168.1.42`'yi ilk metin alanına girin. IP'yi doğru bir şekilde girdikten sonra, GyroWheel Modu veya Oyun Kolu Modu'nu kullanın.

## Bluetooth Modu Bağlantısı

![Bluetooth Listening EXE](readme-photos/bluetooth-listening-exe.png "Bluetooth Listening EXE")

![Bluetooth Listening](readme-photos/bluetooth-listening.png "Bluetooth Listening")

Uygulama, bilgisayarınızın Bluetooth MAC adresini bulup telefon uygulamasına kolayca girmek için görüntülemeyi dener. Eğer adresi algılayamazsa veya birden fazla adres bulursa, doğru adresi manuel olarak girmeniz veya seçmeniz gerekebilir.

![Bluetooth Mode](readme-photos/bluetooth-mode.png "Bluetooth Mode")

Bluetooth modunu kullanmak için, Bluetooth izinlerinin verildiğinden ve Bluetooth destekli bir bilgisayarınız olduğundan emin olun. Bilgisayarınızın MAC adresini girin (yakınlardaki MAC adreslerini almak için "BLUETOOTH'LU BİLGİSAYARLARI ARA" düğmesini kullanabilirsiniz). Bağlantıyı başlatmak için bilgisayarınızda program aktifken "MAC ADRESİNE BAĞLAN" düğmesini kullanın.

![Bluetooth Mode Connected](readme-photos/bluetooth-mode-connected.png "Bluetooth Mode Connected")

Bağlandıktan sonra, GYROWHEEL MODU ve OYUN KOLU MODU düğmeleri kullanıma açılacaktır.

## Ek Bilgiler

![Settings](readme-photos/settings.png "Settings")

Hassasiyet çarpanlarını ve düşük geçiş filtre kaydırıcılarını ayarlayabilir veya tetikleyicileri (LT, RT) analog yapabilirsiniz. Bu, düğmeye basmanın yerine parmağınızı aşağıya doğru hareket ettirmeyi içerir.

Gamepad modunu kullanırken, "TUŞ DÜZENİNİ AYARLA" düğmesini kullanarak buton düzenini düzenleyebilirsiniz. Butonları sürükleyip bırakın veya ölçeklendirmek için sıkıştırın. Özel düzeninizi kullanmak için "Özel Düzeni Kullan" seçeneğini etkinleştirin ve ardından OYUN KOLU MODU düğmesine tıklayın.

"Bağlantı Noktası Yönlendirme" anahtarı, Android'in yerleşik yüksek seviyeli bağlantı işlevini kullanarak bağlanır. Windows'ta "Diğer Bluetooth Ayarları" araması yapın, COM sekmesine gidin ve gelen bir COM portu ekleyin. Windows'un atadığı COM portu değerini girin (varsayılan COM10'dur). Bilgisayarınızda "Start Bluetooth Classic App with Forwarded Port" seçeneğini seçmek için aynı adımları takip edin.

Klavye ve Fare modu yeni eklendi, yakında üzerinde çalışıp daha da geliştireceğim.    
    
Bilgisayarınız artık girişleri almalı ve oyunlarınızda bir denetleyici simüle etmelidir.

Ekranın altındaki düzen düğmesini kullanarak butonları istediğiniz yerlere sürükleyip bırakabilir, ~~hatta ekranın dışında bile yerleştirebilirsiniz~~. Kendi düzeninizi gamepad modunda kullanmak için düzen düğmesinin yanındaki anahtarı etkinleştirin.

Diğer bağlantı yöntemlerini karmaşıklıkları ve yavaşlıkları nedeniyle kaldırdım. Bu deneyim öğretici oldu.

## GyroWheel

Android cihaz ile Windows için Sanal Oyun Kumandası

### Lisans

Bu proje GNU Genel Kamu Lisansı sürüm 3 (GPLv3) altında lisanslanmıştır. GNU GPLv3 şartlarına göre yeniden dağıtabilir ve/veya değiştirebilirsiniz. Ayrıntılar için [LICENSE](LICENSE) dosyasına bakın.

Bu program, yararlı olacağı umuduyla dağıtılmaktadır, ancak HERHANGİ BİR GARANTI VERİLMEMEKTEDİR; Pazar için uygunluk veya belirli bir amaca uygunluk dahil hiçbir garanti verilmemektedir. Daha fazla bilgi için GNU Genel Kamu Lisansını inceleyin.

Bu programla birlikte GNU Genel Kamu Lisansının bir kopyasını almış olmalısınız. Eğer almadıysanız, [https://www.gnu.org/licenses/](https://www.gnu.org/licenses/) adresinden edinin.

### Üçüncü Taraf Lisansları

Bu proje aşağıdaki kütüphaneleri kullanmaktadır:

- **vgamepad**: MIT Lisansı altında lisanslanmıştır. Ayrıntılar için [LICENSES/vgamepad-LICENSE](LICENSES/vgamepad-LICENSE) dosyasına bakın.
- **pyserial**: BSD Lisansı altında lisanslanmıştır. Ayrıntılar için [LICENSES/pyserial-LICENSE](LICENSES/pyserial-LICENSE) dosyasına bakın.

Ayrıca bu sürücüyü kullanmaktadır:

- **ViGEmBus**: BSD Lisansı altında lisanslanmıştır. Ayrıntılar için [LICENSES/vigembus-LICENSE](LICENSES/vigembus-LICENSE) dosyasına bakın.

Bu kütüphanelerin tam lisans metinleri, bu depo içindeki `LICENSES` dizininde bulunmaktadır. Bu projeyi oluşturabilmemi sağladıkları için onlara teşekkür ederim!
