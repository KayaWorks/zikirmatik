# Zikirmatik Sayaç: Google Play'e yükleme rehberi

## Hesabın kapanmaması için gereken (Google'ın kuralı)

Google'a göre etkin olmayan hesabın kapanmaması için **ikisi birden** gerekir:

1. **İletişim bilgilerini doğrulamak:** Play Console > Geliştirici hesabı > Hesap ayrıntıları > e-posta ve telefon doğrulaması > Kaydet.
2. **Bir uygulama paketi (AAB) yüklemek:** Herkese açık yayın şart değil. **Dahili test (internal testing)** kanalına yüklemek de sayılır.

Kaynak: https://support.google.com/googleplay/android-developer/answer/11605267

> Not: 2023 sonrası açılan kişisel hesaplarda **üretime (herkese açık)** çıkmak için 14 gün boyunca 12 testçiyle kapalı test gerekir. Hesabı kurtarmak için buna gerek yok, dahili test yeterli.

Yeni uygulamalar **API 36 (Android 16)** hedeflemeli. Bu proje `targetSdk = 36`.

---

## 1. AAB dosyasını derle (iki yoldan biri)

### Yol A: GitHub Actions (bilgisayarda kurulum gerekmez)
1. GitHub'da boş bir depo aç (ör. `zikirmatik`, özel olabilir).
2. Bu klasörü o depoya gönder (Claude yapabilir).
3. Depo > Settings > Secrets and variables > Actions altına şu 4 sırrı ekle (değerler `zikirmatik-keys` klasöründe):
   - `KEYSTORE_BASE64`: `upload.jks` dosyasının base64 hali
   - `KEYSTORE_PASSWORD`, `KEY_PASSWORD`: `keystore.properties` içindeki şifre
   - `KEY_ALIAS`: `upload`
4. Actions sekmesinde "Build release AAB" çalışır. Bitince **zikirmatik-release-aab** dosyasını indir, içinden `app-release.aab` çıkar.

### Yol B: Android Studio
1. Bu klasörü Android Studio ile aç (SDK 36 kurulu olsun).
2. `zikirmatik-keys/keystore.properties` dosyasını proje köküne kopyala, `storeFile` satırını `upload.jks`'nin tam yoluyla değiştir.
3. Terminal: `gradlew bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`.

> `upload.jks` ve şifresini mutlaka yedekle. Güncelleme yüklemek için gerekir.

## 2. Play Console'da uygulamayı oluştur
1. play.google.com/console > **Uygulama oluştur**
2. Ad: `Zikirmatik Sayaç`, varsayılan dil: Türkçe, Uygulama, Ücretsiz. Beyanları onayla.

## 3. Dahili teste AAB yükle (hesabı kurtaran adım)
1. Test ve yayınla > Test > **Dahili test** > Yeni sürüm oluştur.
2. Play Uygulama İmzalama'yı kabul et (Google'ın yönetmesi önerilir).
3. `app-release.aab` dosyasını yükle, sürüm notu yaz ("İlk sürüm"), Kaydet > İncele > Dahili teste sun.
4. Testçiler sekmesinde kendi e-posta adresini içeren bir liste ekle.

## 4. Uygulama içeriği (Politika > Uygulama içeriği)
| Bölüm | Cevap |
|---|---|
| Gizlilik politikası | `store/privacy-policy.html`'i herkese açık bir adrese koy (GitHub Pages veya Google Sites) ve URL'yi gir. E-posta yer tutucusunu doldur. |
| Uygulama erişimi | Tüm işlevler kısıtlama olmadan kullanılabilir |
| Reklamlar | Hayır, reklam içermiyor |
| İçerik derecelendirmesi | Anket: Kategori "Araçlar/Diğer", tüm sorulara Hayır |
| Hedef kitle | 18 yaş ve üzeri (en sorunsuz seçenek) |
| Veri güvenliği | Veri toplanmıyor/paylaşılmıyor: "Hayır" |
| Haber uygulaması / Sağlık / Finans / Devlet | Hayır |

## 5. Mağaza girişi (Büyüme > Mağaza varlığı > Ana mağaza girişi)
- Metinler: `store/listing-tr.txt` (İngilizce: `store/listing-en.txt`)
- Simge: `store/icon-512.png`
- Öne çıkan görsel: `store/feature-1024x500.png`
- Telefon ekran görüntüleri: `store/screenshot-1..3.png` (Uygulamayı telefonuna kurunca gerçek ekran görüntüleriyle değiştirmen daha iyi olur.)

## 6. Son kontrol
- Panodaki "Uygulamanızı ayarlayın" görevlerinin hepsi yeşil mi?
- Hesap ayrıntılarında e-posta **ve** telefon doğrulandı mı?
- Dahili test sürümü "Kullanıma sunuldu" durumunda mı?

Bu adımlar bitince hesap uyarısı birkaç gün içinde kalkar. Play Console'daki uyarıyı kontrol et.
