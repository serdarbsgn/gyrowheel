# Project Name

## Installation

### English

If you prefer to avoid dealing with Python installation and technical details, you can use the executable files I prepared:

- [Download the x64 executable](https://github.com/serdarbsgn/gyrowheel/raw/main/dist/Runx64.exe)
- [Download the x86 executable](https://github.com/serdarbsgn/gyrowheel/raw/main/dist/Runx86.exe)

You still need to download and install the [ViGEm Bus Driver](https://vigembusdriver.com/download/). Use the correct architecture executable file for your computer. (If one doesn't work, try the other. Be sure to install the ViGEm Bus Driver.)

If you prefer not to use executable files, read below.

![Run .exe](readme-photos/runexe.png "Run .exe")

Use the correct Run .exe to select run options.

### Türkçe

Python kurulumu ve teknik detaylarla uğraşmak istemiyorsanız, hazırladığım çalıştırılabilir dosyaları kullanabilirsiniz:

- [x64 çalıştırılabilir dosyasını indir](https://github.com/serdarbsgn/gyrowheel/raw/main/dist/Runx64.exe)
- [x86 çalıştırılabilir dosyasını indir](https://github.com/serdarbsgn/gyrowheel/raw/main/dist/Runx86.exe)

ViGEm Bus Driver'ı da indirip yüklemeniz gerekecek. Bilgisayarınız için doğru mimarideki çalıştırılabilir dosyayı kullanın. (Birisi çalışmazsa, diğerini deneyin. ViGEm Bus Driver'ı yüklediğinizden emin olun.)

Çalıştırılabilir dosyaları kullanmak istemiyorsanız, aşağıya bakın.

![Run .exe](readme-photos/runexe.png "Run .exe")

Doğru çalıştırılabilir dosyayı kullanarak çalıştırma seçeneklerini seçin.

## Installation Method II

### English

If you have Python 3 installed, [install Python here](https://www.python.org/downloads/), then use `install.bat` to install the necessary libraries. Install the APK on your phone, then connect your computer and phone to the same network. (This is not necessary for Bluetooth mode.)

![Run .bat](readme-photos/run-bat.png "Run .bat")

Use `run.bat` to select run options.

### Türkçe

Python 3 yüklüyse, [Python'u buradan yükleyin](https://www.python.org/downloads/), ardından gerekli kütüphaneleri yüklemek için `install.bat` dosyasını kullanın. APK'yı telefonunuza yükleyin, ardından bilgisayarınızı ve telefonunuzu aynı ağa bağlayın. (Bluetooth modu için bu gerekli değildir.)

![Run .bat](readme-photos/run-bat.png "Run .bat")

Çalıştırma seçeneklerini seçmek için `run.bat` dosyasını kullanın.

## Network Mode Connection

### English

![UDP Mode EXE](readme-photos/run-udp-mode-exe.png "UDP Mode EXE")

To use Network/UDP mode, write the IP displayed on your computer to your phone. For example, `192.168.1.42:12345`.

![UDP Mode](readme-photos/run-udp-mode.png "UDP Mode")

To use Network/UDP mode, write the IP displayed on your terminal to your phone. For example, `192.168.1.42:12345`.

![Enter IP on Phone](readme-photos/udp-mode-enter-ip.png "Enter IP on Phone")

Select the "USE NETWORK" option and enter `192.168.1.42` into the first text field. After correctly entering the IP displayed on your PC, use GyroWheel Mode or Gamepad Mode.

### Türkçe

![UDP Mode EXE](readme-photos/run-udp-mode-exe.png "UDP Mode EXE")

Ağ/UDP modunu kullanmak için, bilgisayarınızda görüntülenen IP'yi telefonunuza yazın. Örneğin, `192.168.1.42:12345`.

![UDP Mode](readme-photos/run-udp-mode.png "UDP Mode")

Ağ/UDP modunu kullanmak için, terminalde görüntülenen IP'yi telefonunuza yazın. Örneğin, `192.168.1.42:12345`.

![Enter IP on Phone](readme-photos/udp-mode-enter-ip.png "Enter IP on Phone")

"AĞ'I KULLAN" seçeneğini seçin ve `192.168.1.42`'yi ilk metin alanına girin. IP'yi doğru bir şekilde girdikten sonra, GyroWheel Modu veya Oyun Kolu Modu'nu kullanın.

## Bluetooth Mode Connection

### English

![Bluetooth Listening EXE](readme-photos/bluetooth-listening-exe.png "Bluetooth Listening EXE")

![Bluetooth Listening](readme-photos/bluetooth-listening.png "Bluetooth Listening")

The application attempts to find and display your computer's Bluetooth MAC address for easy entry in the phone app. If it fails to detect or finds multiple addresses, you may need to enter or select the correct address manually.

![Bluetooth Mode](readme-photos/bluetooth-mode.png "Bluetooth Mode")

To use Bluetooth mode, ensure Bluetooth permissions are granted and that you have a Bluetooth-enabled computer. Enter your PC’s MAC address (you can use the "SHOW BLUETOOTH COMPUTERS" button to get nearby MAC addresses). Use the "CONNECT TO MAC ADDRESS" button to initiate the connection while the script is active on your computer.

![Bluetooth Mode Connected](readme-photos/bluetooth-mode-connected.png "Bluetooth Mode Connected")

Once connected, the GYROWHEEL MODE and GAMEPAD MODE buttons will light up for use.

### Türkçe

![Bluetooth Listening EXE](readme-photos/bluetooth-listening-exe.png "Bluetooth Listening EXE")

![Bluetooth Listening](readme-photos/bluetooth-listening.png "Bluetooth Listening")

Uygulama, bilgisayarınızın Bluetooth MAC adresini bulup telefon uygulamasına kolayca girmek için görüntülemeyi dener. Eğer adresi algılayamazsa veya birden fazla adres bulursa, doğru adresi manuel olarak girmeniz veya seçmeniz gerekebilir.

![Bluetooth Mode](readme-photos/bluetooth-mode.png "Bluetooth Mode")

Bluetooth modunu kullanmak için, Bluetooth izinlerinin verildiğinden ve Bluetooth destekli bir bilgisayarınız olduğundan emin olun. Bilgisayarınızın MAC adresini girin (yakınlardaki MAC adreslerini almak için "BLUETOOTH'LU BİLGİSAYARLARI ARA" düğmesini kullanabilirsiniz). Bağlantıyı başlatmak için bilgisayarınızda program aktifken "MAC ADRESİNE BAĞLAN" düğmesini kullanın.

![Bluetooth Mode Connected](readme-photos/bluetooth-mode-connected.png "Bluetooth Mode Connected")

Bağlandıktan sonra, GYROWHEEL MODU ve OYUN KOLU MODU düğmeleri kullanıma açılacaktır.

## Additional Info

### English

![Settings](readme-photos/settings.png "Settings")

You can adjust sensitivity multipliers and low-pass filter sliders, or make triggers (LT, RT) analog. This involves pressing and moving your finger down rather than simply pressing the button.

When using Gamepad mode, you can edit the button layout using the "EDIT LAYOUT" button. Drag and drop buttons or pinch to scale them. Switch the "Use Custom Layout" toggle on before clicking the Gamepad Mode button to use your custom layout.

The "Forwarded Socket" switch connects using Android’s built-in high-level connect function. In Windows, search for "Other Bluetooth Options," navigate to the COM tab, and add an incoming COM port. Enter the COM port value assigned by Windows (default is COM10). Follow the same steps to select "Start Bluetooth Classic App with Forwarded Port" on your computer.

Your computer should now receive inputs and simulate a controller in your games.

Use the edit layout button at the bottom of the screen to drag and drop buttons to your desired locations, ~~even outside the screen if you don’t want to use them~~. Toggle the switch next to the edit button to use your custom layout in gamepad mode.

I removed several other connection methods due to their complexity and slowness. It was a valuable learning experience.

### Türkçe

![Settings](readme-photos/settings.png "Settings")

Hassasiyet çarpanlarını ve düşük geçiş filtre kaydırıcılarını ayarlayabilir veya tetikleyicileri (LT, RT) analog yapabilirsiniz. Bu, düğmeye basmanın yerine parmağınızı aşağıya doğru hareket ettirmeyi içerir.

Gamepad modunu kullanırken, "TUŞ DÜZENİNİ AYARLA" düğmesini kullanarak buton düzenini düzenleyebilirsiniz. Butonları sürükleyip bırakın veya ölçeklendirmek için sıkıştırın. Özel düzeninizi kullanmak için "Özel Düzeni Kullan" seçeneğini etkinleştirin ve ardından OYUN KOLU MODU düğmesine tıklayın.

"Bağlantı Noktası Yönlendirme" anahtarı, Android'in yerleşik yüksek seviyeli bağlantı işlevini kullanarak bağlanır. Windows'ta "Diğer Bluetooth Ayarları" araması yapın, COM sekmesine gidin ve gelen bir COM portu ekleyin. Windows'un atadığı COM portu değerini girin (varsayılan COM10'dur). Bilgisayarınızda "Start Bluetooth Classic App with Forwarded Port" seçeneğini seçmek için aynı adımları takip edin.

Bilgisayarınız artık girişleri almalı ve oyunlarınızda bir denetleyici simüle etmelidir.

Ekranın altındaki düzen düğmesini kullanarak butonları istediğiniz yerlere sürükleyip bırakabilir, ~~hatta ekranın dışında bile yerleştirebilirsiniz~~. Kendi düzeninizi gamepad modunda kullanmak için düzen düğmesinin yanındaki anahtarı etkinleştirin.

Diğer bağlantı yöntemlerini karmaşıklıkları ve yavaşlıkları nedeniyle kaldırdım. Bu deneyim öğretici oldu.

## GyroWheel

Virtual Gamepad for Windows using an Android device

### License

This project is licensed under the GNU General Public License version 3 (GPLv3). You can redistribute it and/or modify it under the terms of the GNU GPLv3. See the [LICENSE](LICENSE) file for details.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see [https://www.gnu.org/licenses/](https://www.gnu.org/licenses/).

### Lisans

Bu proje GNU Genel Kamu Lisansı sürüm 3 (GPLv3) altında lisanslanmıştır. GNU GPLv3 şartlarına göre yeniden dağıtabilir ve/veya değiştirebilirsiniz. Ayrıntılar için [LICENSE](LICENSE) dosyasına bakın.

Bu program, yararlı olacağı umuduyla dağıtılmaktadır, ancak HERHANGİ BİR GARANTI VERİLMEMEKTEDİR; Pazar için uygunluk veya belirli bir amaca uygunluk dahil hiçbir garanti verilmemektedir. Daha fazla bilgi için GNU Genel Kamu Lisansını inceleyin.

Bu programla birlikte GNU Genel Kamu Lisansının bir kopyasını almış olmalısınız. Eğer almadıysanız, [https://www.gnu.org/licenses/](https://www.gnu.org/licenses/) adresinden edinin.

### Third-Party Licenses

This project uses the following libraries:

- **vgamepad**: Licensed under the MIT License. See [LICENSES/vgamepad-LICENSE](LICENSES/vgamepad-LICENSE).
- **pyserial**: Licensed under the BSD License. See [LICENSES/pyserial-LICENSE](LICENSES/pyserial-LICENSE).

Also using this driver:

- **ViGEmBus**: Licensed under the BSD License. See [LICENSES/vigembus-LICENSE](LICENSES/vigembus-LICENSE).

The full license texts for these libraries are included in the `LICENSES` directory of this repository. It is because of them that I was able to create this project, so THANKS!

### Üçüncü Taraf Lisansları

Bu proje aşağıdaki kütüphaneleri kullanmaktadır:

- **vgamepad**: MIT Lisansı altında lisanslanmıştır. Ayrıntılar için [LICENSES/vgamepad-LICENSE](LICENSES/vgamepad-LICENSE) dosyasına bakın.
- **pyserial**: BSD Lisansı altında lisanslanmıştır. Ayrıntılar için [LICENSES/pyserial-LICENSE](LICENSES/pyserial-LICENSE) dosyasına bakın.

Ayrıca bu sürücüyü kullanmaktadır:

- **ViGEmBus**: BSD Lisansı altında lisanslanmıştır. Ayrıntılar için [LICENSES/vigembus-LICENSE](LICENSES/vigembus-LICENSE) dosyasına bakın.

Bu kütüphanelerin tam lisans metinleri, bu depo içindeki `LICENSES` dizininde bulunmaktadır. Bu projeyi oluşturabilmemi sağladıkları için onlara teşekkür ederim!
