# ŞarkoGuess Geliştirici Devir Dokümanı

Bu doküman, ŞarkoGuess Android müzik tahmin oyunu iskeletini alıp üretime hazır, uçtan uca çalışan bir uygulamaya dönüştürmekle görevlendirilen geliştiriciler için hazırlanmıştır. Tüm kurulum, yapılandırma, entegrasyon ve test adımlarını içerir.

## 1. Proje Genel Bakış
- **Platform:** Android (Min SDK 24), Kotlin, Jetpack Compose, Material 3.
- **Mimari:** Çok modüllü yapı; core, data, domain ve feature katmanları.
- **Bağımlılıklar:** Hilt, Retrofit+Moshi, Room, DataStore, Coroutines/Flow, ExoPlayer, Firebase (Auth, Firestore, Functions, Storage opsiyonel), Detekt, Ktlint, JUnit5, Robolectric, Compose UI Test, Turbine, MockK.
- **Ses Kaynağı:** Yalnızca Apple Music API üzerinden gelen `previewUrl` (30/90 sn) kullanılabilir. Tam parça çalınması yasaktır.
- **Attribution:** UI içinde Apple Music logosu ve "Data provided by Apple Music" dipnotu gösterilir. Şarkı satırında Apple Music derin bağlantısı bulunur.

## 2. Depo Yapısı ve Modül Sorumlulukları
```
app/                        # Uygulama girişi, navigation, Hilt setup, compose host
core/common/                # Ortak Result, dispatcher sağlayıcıları
core/designsystem/          # Tema, tipografi, renkler
core/network/               # Retrofit servisleri, DTO'lar, ağ DI modülü
core/database/              # Room DAO ve entity'leri
core/player/                # ExoPlayer önizleme oynatıcı sarmalayıcısı

data/applemusic/            # Apple Music repository, token sağlayıcı
data/firebase/              # Firebase auth, oyun ve leaderboard repository'leri

domain/                     # Model, repository interface, use case katmanı

feature/onboarding/         # Login/onboarding UI ve ViewModel
feature/artistpicker/       # Sanatçı arama/seçim deneyimi
feature/category/           # Tür ve dönem filtre ekranları
feature/solo/               # Solo oyun akışı ve ExoPlayer kontrol UI'sı
feature/versus/             # 1v1 eşleşme lobisi ve durum takibi
feature/leaderboard/        # Global/haftalık leaderboard ekranı
feature/settings/           # Dil, ses, gizlilik, attribution ayarları

firebase/firestore.rules    # Firestore güvenlik kuralları taslağı
functions/                  # Cloud Functions (TypeScript) kaynağı
README.md                   # Genel proje açıklaması ve hızlı başlangıç
DEVELOPER_HANDOFF.md        # (Bu doküman) Geliştirici odaklı kurulum adımları
```

## 3. Ön Koşullar
- Android Studio **Giraffe** veya üzeri, Android Gradle Plugin 8.1 uyumlu.
- **JDK 17** (Android Studio ile gelen Embedded JDK kullanılabilir).
- Android SDK 34, Android Emulator (Pixel 5+ önerilir) veya fiziksel cihaz.
- **Node.js 18+** ve **npm** (Cloud Functions için).
- **Firebase CLI** (`npm install -g firebase-tools`) ve yetkilendirilmiş Google hesabı.
- Apple Developer hesabı (Music API için developer token üretmek adına).

## 4. Gizli Anahtarlar ve Yapılandırma
1. `local.properties` dosyasına aşağıdaki anahtarları ekleyin:
   ```properties
   APPLE_MUSIC_DEV_TOKEN=<Apple Developer Token>
   APPLE_STOREFRONT=tr        # İsteğe bağlı olarak "us", "de" vb. değiştirebilirsiniz
   FIREBASE_WEB_API_KEY=<Firebase Web API Key - opsiyonel UI mesajları için>
   ```
   > Not: Token oluşturma adımları README.md içerisinde detaylandırılmıştır. Token'ı kesinlikle kaynak koduna gömmeyin.

2. Firebase projesi oluşturup Android uygulamasını ekleyin. `app/google-services.json` dosyasını kendi projenizden alın ve mevcut placeholder dosyasının üzerine yazın (repo'ya dahil etmeyin).

3. Firestore ve Functions region ayarları `functions/src/index.ts` dosyasında `europe-west1` olarak ayarlıdır. Farklı bir bölge kullanacaksanız hem Functions kodunu hem de Android tarafındaki callable function referanslarını güncelleyin.

## 5. Android Projesini Çalıştırma
1. Depoyu klonlayın ve Android Studio'da açın.
2. Gradle sync tamamlandıktan sonra `Build > Make Project` çalıştırarak bağımlılıkların indirildiğinden emin olun.
3. Emülatör veya cihaz bağlayıp `Run 'app'` seçeneğiyle uygulamayı başlatın.
4. Uygulama ilk açılışta anonim Firebase Auth ile oturum açar; eğer Firebase yapılandırması eksikse logcat'te hata göreceksiniz.

## 6. Apple Music API Kullanımı
- **Arama:** `AppleMusicService.searchArtists()` fonksiyonu ile sanatçı araması yapılır.
- **Sanatçı Şarkıları:** `AppleMusicService.getArtistTopSongs()` endpoint'i kullanılır; `previewUrl` alanı dolu parçalardan sorular üretilir.
- Token `TokenProvider` üzerinden `local.properties` dosyasından okunur ve `Authorization: Bearer <token>` header'ına eklenir.
- Gerektiğinde Moshi DTO'larına yeni alanlar ekleyerek genişletme yapabilirsiniz.

## 7. Firebase Cloud Functions
### Geliştirme Ortamı
```bash
cd functions
npm install
npm run build   # TypeScript -> JavaScript derlemesi
firebase emulators:start   # Yerelde test için (Firestore + Functions)
```

### Dağıtım
```bash
firebase login                      # bir kez
firebase use <your-project-id>
firebase deploy --only functions:findOrCreateMatch,functions:generateQuestions,functions:submitAnswer,functions:finalizeMatch
firebase deploy --only firestore:rules
```

### Önemli Fonksiyonlar
- `findOrCreateMatch`: 1v1 eşleştirme için lobi oluşturur veya mevcut lobiyi döner.
- `generateQuestions`: Sanatçıya ait 15 soruluk seti oluşturur, doğru cevapları maskeler.
- `submitAnswer`: Server-side doğrulama ve skor hesaplama (base + timeBonus + streakBonus).
- `finalizeMatch`: Maç bittiğinde Firestore leaderboard koleksiyonlarını günceller.

Fonksiyonlar Apple Music API çağrılarını proxy'ler ve Firestore dokümanlarını `matches/{matchId}` altında saklar. Rate limit ve temel doğrulama kontrolleri bulunmaktadır; gerektiğinde genişletin.

## 8. Firestore Güvenlik Kuralları
- `firebase/firestore.rules` dosyasını Firebase projenize deploy edin.
- Kurallar; kullanıcıların sadece kendi match verilerine sınırlı yazma yetkisi olmasını, skorların ise sadece Cloud Functions tarafından güncellenmesini sağlar.
- Geliştirirken Firebase Emulator Suite üzerinden test etmeniz önerilir.

## 9. Test ve Kalite Kontrolleri
Android tarafında:
```bash
./gradlew ktlintCheck
./gradlew detekt
./gradlew test                # Unit testler (JUnit5, domain use case örnekleri)
./gradlew connectedAndroidTest # Compose UI instrumentasyon testleri
```

Cloud Functions tarafında (jest veya firebase-functions-test ekleyebilirsiniz):
```bash
cd functions
npm test
```
> Not: Varsayılan proje jest yapılandırması içermez; entegrasyonu sağlamak geliştiriciye bırakılmıştır.

## 10. Özelleştirme ve Geliştirme Notları
- Soru seti üretim mantığı `BuildQuestionsUseCase` içinde bulunur. Tür (`genreNames`) ve dönem (`releaseDate.year`) filtreleri uygulanır; gerekirse sayfalama ekleyin.
- ExoPlayer sarmalayıcısı `core/player` modülündedir. Lifecycle-aware kullanım için ViewModel'de `PreviewPlayer` örneği tutulur.
- 1v1 senaryosunda bağlantı kopmaları için 10 sn bekleme ve hükmen galibiyet kuralı Functions tarafında uygulanır; gerekirse client tarafında da kullanıcı mesajları ekleyin.
- Çok dillilik (TR/EN) `strings.xml` dosyalarında örneklenmiştir. Yeni metinler eklerken her iki dil dosyasını güncellemeyi unutmayın.

## 11. Yapılacaklar Kontrol Listesi
- [ ] Apple Music developer token'ı oluşturup `local.properties` dosyasına yazıldı.
- [ ] Firebase projesi kuruldu, `google-services.json` eklendi.
- [ ] Firestore ve Functions emülatörleriyle temel akış test edildi.
- [ ] Gerçek cihaz/emülatörde solo oyun akışı doğrulandı; preview sesleri sorunsuz çalıyor.
- [ ] 1v1 eşleştirme ve skor güncellemeleri Firestore üzerinden çalışıyor.
- [ ] Lider tablosu (weekly & all-time) güncelleniyor ve Cloud Functions tarafından yazılıyor.
- [ ] Detekt ve Ktlint raporları temiz.
- [ ] README ve uygulama içindeki Apple Music attribution öğeleri tasarım gereksinimlerini karşılıyor.

## 12. İletişim ve Destek
- Apple Music API belgeleri: https://developer.apple.com/documentation/applemusicapi
- Firebase Firestore + Functions belgeleri: https://firebase.google.com/docs
- Herhangi bir ek gereksinim veya soru için proje yöneticisi / ürün sahibi ile iletişime geçin.

---
Bu doküman geliştiriciye teslim edilebilir nitelikte tüm adımları içerir. Adımları sırasıyla uygulayarak ŞarkoGuess iskeletini üretime hazır bir uygulamaya dönüştürebilirsiniz.
