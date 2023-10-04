#include <WiFi.h>
#include <SPI.h>
#include <MFRC522.h>
#include <FirebaseESP32.h>
#include <LiquidCrystal_I2C.h>
#include <Wire.h>
#include <Keypad.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

WiFiUDP udp;
NTPClient timeClient(udp, "pool.ntp.org");


//~~~~~~~~~~~~~~~~~~Wifi dan Fire Base~~~~~~~~~~~~~~~~~~//
#define WIFI_SSID "Muskin Asikin"
#define WIFI_PASSWORD "C4tur26TBL"
//#define WIFI_SSID "vivo 1915"
//#define WIFI_PASSWORD "Sandi971"

#define API_KEY "rinYffLcGIuSRHfNheFicZM3izSNNjKHDOSuKigD"
#define DATABASE_URL "https://sanslokerpenyimpananumun-default-rtdb.asia-southeast1.firebasedatabase.app/"
FirebaseData firebaseData;

FirebaseJson idSimpan;
FirebaseJson statusKotak;
FirebaseJson startTime;
FirebaseJson startHours;
FirebaseJson statusPembayaran;
FirebaseJson startday;

FirebaseConfig config;
String Lokasifbdo = "";
//String Lokasifbdo1 = "Loker1";
//String Lokasifbdo2 = "Loker2";

 String waktu = "";
 String jam = "";
 String mnt = "";

//~~~~~~~~~~~~~~ define pin RFID Reader~~~~~~~~~~~~~//

#define SS_PIN  3  // ESP32 pin GIOP5 
//#define SS2  4  //jika menggunakan 2 rfid reader
#define RST_PIN 5 // ESP32 pin GIOP27 

//#define Jml_Reader 2

//array yang menampung rfid pin SS
//byte SS_PIN[] = {SS1, SS2};

//variabel jumlah rfid
//MFRC522 mfrc522 [Jml_Reader];
MFRC522 mfrc522(SS_PIN, RST_PIN);

//int reader;
String IDTAG;
String idSave1, idConfirm1; 
int kondisiPembayaran1;

//~~~~~~~~~~~~~~ define pin Keypad~~~~~~~~~~~~~//

#define ROW_NUM 4 // Jumlah baris pada keypad
#define COLUMN_NUM 4 // Jumlah kolom pada keypad

char keys[ROW_NUM][COLUMN_NUM] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};

byte pin_rows[ROW_NUM] = {32, 33, 25, 26}; // Pin baris pada keypad
byte pin_column[COLUMN_NUM] = {27, 14, 12, 13}; // Pin kolom pada keypad

Keypad keypad = Keypad(makeKeymap(keys), pin_rows, pin_column, ROW_NUM, COLUMN_NUM); // Membuat objek Keypad
String inputNumber = ""; // Variabel untuk menyimpan pilihan nomor
int numDigits = 0;
char enteredNumber[+ 1];

//~~~~~~~~~~~~~~~~~~lcd~~~~~~~~~~~~~~~~~~//
  LiquidCrystal_I2C lcd(0x27, 20, 4);

//~~~~~~~~~~~~~~~~~~Relay~~~~~~~~~~~~~~~~~~//
  const int rly1= 4; 
  const int rly2= 2; 


//###############Void Setup######################//

void setup() {
  Serial.begin(115200);
//************Setup RFID****************************//
  SPI.begin(); // init SPI bus
  mfrc522.PCD_Init();

// initialize the lcd
  lcd.init();
  lcd.backlight();
  
//inisialisasi Relay
  pinMode(rly1, OUTPUT);
  pinMode(rly2, OUTPUT);

//wifi konfigurasi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Menghubungkan Wi-Fi");
  lcd.setCursor(0,3);
  lcd.print("Menghubungkan Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("terhubung dengan IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  Firebase.begin(DATABASE_URL, API_KEY);
  Firebase.reconnectWiFi(true);

  //Set database read timeout to 1 minute (max 15 minutes)
  Firebase.setReadTimeout(firebaseData, 1000 * 60);
  //tiny, small, medium, large and unlimited.
  //Size and its write timeout e.g. tiny (1s), small (10s), medium (30s) and large (60s).
  Firebase.setwriteSizeLimit(firebaseData, "tiny");

  lcd.setCursor(0,3);
  lcd.print(" Terhubung Internet ");
  delay(2000);
  lcd.clear();
  
  timeClient.begin();
  timeClient.setTimeOffset(7 * 3600);
  
  //************Setup RFID****************************//
//  SPI.begin(); // init SPI bus
//  mfrc522.PCD_Init();

//  for (reader = 0; reader < Jml_Reader; reader++) {
//    mfrc522[reader].PCD_Init(SS_PIN[reader], RST_PIN);
//    //    //cek status
//    //    Serial.print("Reader" + String(reader));
//    //    Serial.print(": ");
//    //    mfrc522[reader].PCD_DumpVersionToSerial();
//  }

  Serial.println("Taruh Kartu dan Masukkan pilihan nomor");
  lcd.setCursor(0,0);
  lcd.print("Taruh Kartu Anda dan");
  lcd.setCursor(0,1);
  lcd.print("Masukkan Nomor Loker");

}


//###############Void loop######################//
void loop() {
  timeClient.update();
//  if (timeClient.getMinutes() == 0 && timeClient.getSeconds() == 0) {
//    // Tampilkan waktu setiap satu menit

    unsigned long epochTime = timeClient.getEpochTime();
    struct tm *ptm = gmtime ((time_t *)&epochTime); 
  
    // Mendapatkan tanggal
    int monthDay = ptm->tm_mday;
    jam = String(timeClient.getHours());
    mnt = String(timeClient.getMinutes());
    waktu = jam + ":" + mnt;
//  }
 
  char key = keypad.getKey();
  if (key) {
     Serial.println("Tombol ditekan: " + String(key));
     lcd.setCursor(0,3);
     lcd.print("Angka ditekan:" + String(key));
  
    if (key == '#') {
//      Serial.println("Nomor yang dipilih: " + inputNumber);
      Serial.println("Sedang Memproses ");
      lcd.clear();
      lcd.setCursor(0,1);
      lcd.print("  Sedang Memproses  ");
      lcd.setCursor(0,2);
      lcd.print("      " + Lokasifbdo + "      ");
     
       processNumber(inputNumber);
       Serial.println("lokasi fbdo :" + Lokasifbdo);
       Serial.println("Tempelkan Kartu");
        Serial.println("id :");
          if (Firebase.RTDB.getString(&firebaseData, "/Depok/" + Lokasifbdo + "/idSimpan")) {
          if (firebaseData.dataType() == "string") {
            idSave1 = firebaseData.stringData();
                      Serial.print("id :");
                      Serial.println(idSave1);
          }
        }
      
          if (Firebase.RTDB.getInt(&firebaseData, "/Depok/" + Lokasifbdo + "/statusPembayaran")) {
            if (firebaseData.dataType() == "int") {
              kondisiPembayaran1 = firebaseData.intData();
              Serial.println("Kondisi Pembayaran :");
               Serial.println(kondisiPembayaran1);
            }
          }
//          else {
//          Serial.println(firebaseData.errorReason());
//        }

//        for (int reader = 0; reader < Jml_Reader; reader++) {
//        if (mfrc522[reader].PICC_IsNewCardPresent() && mfrc522[reader].PICC_ReadCardSerial()) {
          if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
          IDTAG = "";
//          for (byte i = 0; i < mfrc522[reader].uid.size; i++) {
          for (byte i = 0; i < mfrc522.uid.size; i++) {
//            IDTAG += mfrc522[reader].uid.uidByte[i];
              IDTAG.concat(String(mfrc522.uid.uidByte[i]));
          }
//          Serial.print("Reader" + String(reader));
//          Serial.print(": ");
          Serial.println("UID :" + IDTAG);
//          if (idSave1 == IDTAG && reader == 0 && kondisiPembayaran1 == 1) {
          if (idSave1 == IDTAG && kondisiPembayaran1 == 1) {
            String IDNULL = "null";
            int nilaiPem = 0;
            
            lcd.clear();
            lcd.setCursor(0,1);
            lcd.print("      BERHASIL      ");
            lcd.setCursor(0,2);
            lcd.print(" Ambil Barang Anda");
            
            if (Lokasifbdo == "Loker1"){
            digitalWrite(rly1, HIGH); //buka
            } else if(Lokasifbdo == "Loker2"){
            digitalWrite(rly2, HIGH); //buka
            }
            
            idSimpan.set("idSimpan", IDNULL);
            Firebase.updateNode(firebaseData, "Depok/" + Lokasifbdo, idSimpan);
            statusPembayaran.set("statusPembayaran", nilaiPem);
            Firebase.updateNode(firebaseData, "Depok/" + Lokasifbdo, statusPembayaran);
            statusKotak.set("statusKotak", 2);
            Firebase.updateNode(firebaseData,"Depok/" + Lokasifbdo, statusKotak);
            startday.set("startday", 0);
            Firebase.updateNode(firebaseData, "Depok/" + Lokasifbdo, startday);
//            mfrc522[reader].PICC_HaltA();
//            mfrc522[reader].PCD_StopCrypto1();
            mfrc522.PICC_HaltA();
            delay(2000);
          }
    
          else if (idSave1 == IDTAG && kondisiPembayaran1 == 0) {
            Serial.println("Maaf Lakukan pembayaran dahulu");
            lcd.clear();
            lcd.setCursor(0,1);
            lcd.print("     Anda Belum     ");
            lcd.setCursor(0,2);
            lcd.print("Melakukan Pembayaran");
            mfrc522.PICC_HaltA();
            delay(2000);
          }
          
          else if (idSave1 == "null" && kondisiPembayaran1 == 0) {
            lcd.clear();
            lcd.setCursor(0,1);
            lcd.print("      BERHASIL      ");
            lcd.setCursor(0,2);
            lcd.print("   Loker Terkunci   ");

            if (Lokasifbdo == "Loker1"){
            digitalWrite(rly1, LOW); //tutup
            } else if(Lokasifbdo == "Loker2"){
            digitalWrite(rly2, LOW); //tutup
            }
            
            int startjam = jam.toInt();
            idSimpan.set("idSimpan", IDTAG);
            Firebase.updateNode(firebaseData,"Depok/" + Lokasifbdo, idSimpan);
            statusKotak.set("statusKotak", 1);
            Firebase.updateNode(firebaseData,"Depok/" + Lokasifbdo, statusKotak);
            startTime.set("startTime", waktu);
            Firebase.updateNode(firebaseData, "Depok/" +Lokasifbdo, startTime);
            startHours.set("startHours", startjam);
            Firebase.updateNode(firebaseData, "Depok/" + Lokasifbdo, startHours);
            startday.set("startday", monthDay);
            Firebase.updateNode(firebaseData, "Depok/" + Lokasifbdo, startday);
            
            mfrc522.PICC_HaltA();
            delay(2000);
          }
          else {
            Serial.println("UID :" + IDTAG);
            Serial.println("Maaf Beda Kartu");
            lcd.clear();
            lcd.setCursor(0,1);
            lcd.print("  Maaf Beda Kartu");
            mfrc522.PICC_HaltA();
            delay(2000);
          }
          Serial.println("Taruh Kartu dan Masukkan pilihan nomor");
          Lokasifbdo = "";
          lcd.clear();
          lcd.setCursor(0,1);
          lcd.print("  Ambil Kartu Anda  ");
          delay (5000);
          lcd.clear();
          lcd.setCursor(0,0);
          lcd.print("Taruh Kartu Anda dan");
          lcd.setCursor(0,1);
          lcd.print("Masukkan Nomor Loker");
//          mfrc522.PCD_StopCrypto1();
          delay(1000);
        }

        delay(1000);
//      }
      
        inputNumber = ""; // Menghapus isi variabel inputNumber
    }
    else if (key == '*') {
      // Menghapus digit terakhir jika tombol delete ('*') ditekan
      inputNumber = "";
      Serial.println("Nomor yang dipilih: 0");
      lcd.setCursor(0,3);
      lcd.print("Angka ditekan:   ");
      
    }
    else if (key == 'D'){
      mfrc522.PICC_HaltA();
       ESP.restart();
    }
    else {
      inputNumber += key; // Menambahkan karakter ke variabel inputNumber
      Serial.println("Nomor yang dipilih: " + inputNumber);
      lcd.setCursor(0,3);
      lcd.print("Angka ditekan:" + inputNumber);
    }
  }
delay(500);
}

void processNumber(String number) {
         if (number == "1") {
        Serial.println("Pilihan nomor 1 diproses");
        // Tindakan untuk nomor 1
        Lokasifbdo ="Loker1";
      } else if (number == "2") {
        Serial.println("Pilihan nomor 2 diproses");
        // Tindakan untuk nomor 2
        Lokasifbdo ="Loker2";
      } else if (number == "3") {
        Serial.println("Pilihan nomor 3 diproses");
        // Tindakan untuk nomor 3
        Lokasifbdo ="Loker3";
      } else if (number == "4") {
        Serial.println("Pilihan nomor 4 diproses");
        // Tindakan untuk nomor 4
        Lokasifbdo ="Loker4";
      } else if (number == "5") {
        Serial.println("Pilihan nomor 5 diproses");
        // Tindakan untuk nomor 5
        Lokasifbdo ="Loker5";
      } 
      else if (number == "6") {
        Serial.println("Pilihan nomor 6 diproses");
        // Tindakan untuk nomor 6
        Lokasifbdo ="Loker6";
      } 
      else if (number == "7") {
        Serial.println("Pilihan nomor 7 diproses");
        // Tindakan untuk nomor 7
        Lokasifbdo = "Loker7";
      } 
      else if (number == "8") {
        Serial.println("Pilihan nomor 8 diproses");
        // Tindakan untuk nomor 8
        Lokasifbdo ="Loker8";
      } 
      else if (number == "9") {
        Serial.println("Pilihan nomor 9 diproses");
        // Tindakan untuk nomor 9
        Lokasifbdo ="Loker9";
      } 
      else if (number == "10") {
        Serial.println("Pilihan nomor 10 diproses");
        // Tindakan untuk nomor 10
        Lokasifbdo ="Loker10";
      } 
      else if (number == "11") {
        Serial.println("Pilihan nomor 11 diproses");
        // Tindakan untuk nomor 11
        Lokasifbdo ="Loker11";
      } 
      else if (number == "12") {
        Serial.println("Pilihan nomor 12 diproses");
        // Tindakan untuk nomor 12
        Lokasifbdo ="Loker12";
      } 
      else {
        Serial.println("Nomor tidak valid");
        // Tindakan untuk nomor tidak valid
      }
}
