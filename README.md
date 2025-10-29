# ŞarkoGuess

ŞarkoGuess, Apple Music kataloğundan 30/90 saniyelik `previewUrl` parçalarını kullanarak şarkıyı tahmin etme üzerine kurulu bir Android oyunu iskeletidir. Proje; Jetpack Compose tabanlı modern UI, Hilt ile bağımlılık enjeksiyonu, Firebase arka uç entegrasyonu ve Apple Music REST API tüketimi için hazır modüler mimari sunar.

## Modüler Mimari

```
app/
core/designsystem/
core/common/
core/network/
core/database/
core/player/
data/applemusic/
data/firebase/
domain/
feature/onboarding/
feature/artistpicker/
feature/category/
feature/solo/
feature/versus/
feature/leaderboard/
feature/settings/
functions/
firebase/
```

- `core` katmanları tasarım sistemi, ortak yardımcılar, ağ/Room kurulumları ve ExoPlayer sarıcılarını barındırır.
- `data` katmanları Apple Music REST istemcisi ve Firebase kaynaklarını içerir.
- `domain` katmanı model/veri sınıfları ve skor hesaplama ile soru üretim use-case'lerini sağlar.
- `feature-*` modülleri ekran ViewModel ve Compose UI bileşenlerini içerir.
- `functions` klasörü Firebase Cloud Functions (TypeScript) kodlarını barındırır.

## Başlangıç

### Gereksinimler

- Android Studio Giraffe veya daha günceli
- JDK 17
- Android SDK 34
- Firebase CLI (Cloud Functions için)

### Apple Music Geliştirici Tokenı

Apple Music API çağrıları için [Apple Developer hesabınızdan](https://developer.apple.com/documentation/applemusicapi/getting_keys_and_creating_tokens) JWT tabanlı geliştirici tokenı üretin. `local.properties` dosyasına aşağıdaki anahtarları ekleyin:

```
APPLE_MUSIC_DEV_TOKEN=eyJhbGciOiJIUzI1NiIs...
APPLE_STOREFRONT=tr
```

Token uygulama açılışında `BuildConfig` üzerinden `TokenProvider` içerisine aktarılır.

### Firebase Kurulumu

1. Firebase projesi oluşturun.
2. `com.sharkoguess` paket adıyla Android uygulaması ekleyip oluşan `google-services.json` dosyasını `app/` klasörüne yerleştirin.
3. Firestore, Authentication ve Functions servislerini etkinleştirin.
4. `firebase/firestore.rules` dosyasını konsoldan içe aktarın veya CI sırasında dağıtın.
5. `functions/` klasöründe aşağıdaki komutlarla Cloud Functions'ı derleyin ve dağıtın:

```
cd functions
npm install
npm run build
firebase deploy --only functions
```

Cloud Functions şunları sağlar:
- `generateQuestions`: Sunucu tarafında tür/dönem filtreleriyle 15 soruluk set oluşturur.
- `findOrCreateMatch`: 1v1 eşleşmeleri hızlıca oluşturur veya bekleyen oyuncuya katılır.
- `submitAnswer` / `submitSoloAnswer`: Yanıtları doğrular ve puanı (süre + seri bonusu) hesaplar.
- `finalizeMatch`: Maç sonuçlarını Firestore'a yazar ve lider tablosunu günceller.
- `updateLeaderboard`: Haftalık rollover için zamanlanmış bakım örneği.

### Çalıştırma

Android Studio'da projeyi açıp Gradle senkronizasyonunu bekleyin. Minimum API 24, hedef API 34 olacak şekilde emülatör ya da cihazda çalıştırabilirsiniz. Solo modda Apple Music'ten gelen önizleme URL'leri ExoPlayer üzerinden çalınır.

### Testler

- Domain katmanı JUnit testi: `CalculateScoreUseCaseTest` skor hesaplaması senaryolarını doğrular.
- Compose UI testi örneği: `MainActivityTest` onboarding metninin göründüğünü kontrol eder.

Komut satırından çalıştırmak için:

```
./gradlew test
./gradlew connectedAndroidTest
```

### Kod Kalitesi

Projede Detekt ve Ktlint plug-in'leri tanımlıdır. Geliştirme sırasında

```
./gradlew detekt
./gradlew ktlintCheck
```

komutları ile statik analiz ve stil kontrolü yapabilirsiniz.

## Apple Music Attribution

Uygulama alt bölümünde Apple Music logosu ve "Data provided by Apple Music" ibaresi gösterilir. Her soruda "Apple Music ile aç" derin bağlantısı için `Track.appleUrl` alanı kullanıma hazırdır.

## Notlar

- Gerçek Apple Music veri akışı için Functions tarafında Apple Music API çağrılarını proxy'lemek veya daha kapsamlı önbellekleme stratejisi eklemek gereklidir.
- Firebase güvenlik kuralları örnek bir temel sağlar; prodüksiyon ortamında oran sınırlama ve ek doğrulamalar önerilir.
- Gizli anahtarları kaynağa dahil etmeyin; `local.properties`, CI gizli değişkenleri veya Android Keystore kullanın.

