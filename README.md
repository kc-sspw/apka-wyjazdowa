> **Status w KC SSPW: materiał referencyjny / prototyp, nie produkt wdrożeniowy.**
>
> Repozytorium zawiera kod projektu **Poligon** udostępniony jako materiał wyjściowy do analizy przy rozwijaniu większej aplikacji wyjazdowej. Głównym repozytorium planowanego produktu Komisji jest **[kc-sspw/appka-eventowa](https://github.com/kc-sspw/appka-eventowa)** (repozytorium prywatne).
>
> Wykorzystanie elementów tego kodu w nowym produkcie wymaga osobnego przeglądu wymagań, bezpieczeństwa, praw do kodu i decyzji architektonicznej. Samo umieszczenie kodu w organizacji nie oznacza zatwierdzenia technologii ani gotowości produkcyjnej.
>
> **Uwaga:** dokumentacja poniżej opisuje zastany projekt Poligon i nie stanowi instrukcji wdrożenia docelowego systemu KC.

---

# Poligon Backend

Backend aplikacji **Poligon**, napisany w Java i Spring Boot. Aplikacja udostępnia REST API do zarządzania danymi wykorzystywanymi przez frontend, obsługuje uwierzytelnianie przez Firebase, aktualizacje w czasie rzeczywistym przez WebSocket oraz integrację z SMSAPI.

## Główne funkcje

- zarządzanie ekipą (`Crew`),
- zarządzanie harmonogramem,
- obsługa ogłoszeń, zadań dziennych, zespołów i pozostałych danych aplikacji,
- publiczne operacje odczytu i uwierzytelnione operacje modyfikujące dane,
- weryfikacja tokenów Firebase,
- dodatkowa weryfikacja użytkowników na podstawie listy adresów e-mail w Firestore,
- aktualizacje danych w czasie rzeczywistym przez WebSocket,
- integracja z SMSAPI,
- obsługa sprawdzania zmian po parametrze `lastUpdated`.

## Technologie

- Java
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Firebase Authentication
- Google Cloud Firestore
- WebSocket
- SMSAPI
- Maven Wrapper
- Lombok

## Struktura projektu

```text
src/main/java/me/havooc/poligon
├── controller      # REST API
├── model           # encje JPA i modele danych
├── repository      # repozytoria Spring Data JPA
├── security        # Firebase, Spring Security, CORS i WebSocket
└── service         # logika usług, m.in. SMSAPI i lista dozwolonych użytkowników
```

## Wymagania

Do uruchomienia projektu potrzebne są:

- JDK zgodne z wersją skonfigurowaną w `pom.xml`,
- dostęp do bazy PostgreSQL,
- projekt Firebase,
- dane konta serwisowego Firebase zakodowane w Base64,
- token SMSAPI.

Maven nie musi być instalowany globalnie — projekt zawiera Maven Wrapper.

## Konfiguracja

Konfiguracja aplikacji znajduje się w:

```text
src/main/resources/application.properties
```

Sekrety nie są przechowywane w repozytorium. Aplikacja oczekuje następujących zmiennych środowiskowych:

| Zmienna | Opis |
|---|---|
| `DB_URL` | JDBC URL bazy PostgreSQL |
| `DB_USER` | użytkownik bazy danych |
| `DB_PASSWORD` | hasło do bazy danych |
| `SMSAPI_TOKEN` | token API usługi SMSAPI |
| `FIREBASE_SERVICE_ACCOUNT_BASE64` | zawartość pliku danych konta serwisowego Firebase zakodowana w Base64 |

Przykładowa konfiguracja znajduje się w pliku:

```text
.env.example
```

> Spring Boot nie tworzy ani nie odczytuje automatycznie pliku `.env`. Zmienne środowiskowe należy ustawić w systemie, konfiguracji uruchomieniowej IDE, Docker Compose lub środowisku hostingowym.

## Przykładowe zmienne środowiskowe

```env
DB_URL=jdbc:postgresql://localhost:5432/poligon
DB_USER=your_username
DB_PASSWORD=your_password

SMSAPI_TOKEN=your_smsapi_token

FIREBASE_SERVICE_ACCOUNT_BASE64=your_base64_encoded_firebase_service_account
```

## Konfiguracja Firebase

Aplikacja nie wymaga pliku:

```text
firebase-service-account.json
```

w repozytorium ani w katalogu `resources`.

Zawartość pliku danych konta serwisowego Firebase jest przekazywana przez zmienną:

```text
FIREBASE_SERVICE_ACCOUNT_BASE64
```

Aplikacja:

1. odczytuje zmienną środowiskową,
2. dekoduje Base64,
3. tworzy dane uwierzytelniające Firebase w pamięci,
4. inicjalizuje Firebase Admin SDK.

Dzięki temu prywatny klucz Firebase nie znajduje się w repozytorium ani w gotowym pliku JAR.

### Kodowanie pliku do Base64

#### macOS / Linux

```bash
base64 < firebase-service-account.json | tr -d '\n'
```

#### Windows PowerShell

```powershell
[Convert]::ToBase64String(
    [IO.File]::ReadAllBytes("firebase-service-account.json")
)
```

Wygenerowany ciąg należy ustawić jako wartość:

```text
FIREBASE_SERVICE_ACCOUNT_BASE64
```

Base64 nie jest szyfrowaniem — zakodowaną wartość należy traktować jak sekret.

## Firestore — dozwoleni użytkownicy

Po poprawnym zweryfikowaniu tokenu Firebase backend dodatkowo sprawdza, czy adres e-mail użytkownika znajduje się na liście dozwolonych adresów w Firestore.

Oczekiwana struktura:

```text
collection: allowedEmails
document:   data
field:      emails
```

Pole `emails` powinno być tablicą adresów e-mail.

Lista jest przechowywana w pamięci aplikacji i odświeżana co godzinę.

## Uruchomienie lokalne

### 1. Sklonuj repozytorium

```bash
git clone <repository-url>
cd <repository-directory>
```

### 2. Ustaw wymagane zmienne środowiskowe

#### macOS / Linux

```bash
export DB_URL="jdbc:postgresql://localhost:5432/poligon"
export DB_USER="postgres"
export DB_PASSWORD="your_password"
export SMSAPI_TOKEN="your_token"
export FIREBASE_SERVICE_ACCOUNT_BASE64="your_base64_value"
```

#### Windows PowerShell

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/poligon"
$env:DB_USER="postgres"
$env:DB_PASSWORD="your_password"
$env:SMSAPI_TOKEN="your_token"
$env:FIREBASE_SERVICE_ACCOUNT_BASE64="your_base64_value"
```

### 3. Uruchom aplikację

#### macOS / Linux

```bash
./mvnw spring-boot:run
```

#### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

## Budowanie projektu

### macOS / Linux

```bash
./mvnw clean package
```

### Windows

```powershell
.\mvnw.cmd clean package
```

Gotowy plik aplikacji zostanie wygenerowany w katalogu:

```text
target/
```

## Uwierzytelnianie i autoryzacja

Backend używa tokenów Firebase przesyłanych w nagłówku:

```http
Authorization: Bearer <firebase-id-token>
```

Aktualne zasady dostępu:

- żądania `GET` są publiczne,
- `/api/crew/me` wymaga uwierzytelnienia,
- operacje modyfikujące dane wymagają uwierzytelnienia,
- użytkownik musi znajdować się na liście dozwolonych adresów e-mail w Firestore.

Aplikacja działa bezstanowo (`STATELESS`) i nie korzysta z sesji serwerowych.

## Wybrane endpointy

Poniższa lista obejmuje endpointy widoczne w przeanalizowanej części projektu.

### Crew

```text
GET    /api/crew
GET    /api/crew/me
POST   /api/crew
DELETE /api/crew/{id}
```

`GET /api/crew` może przyjąć opcjonalny parametr:

```text
lastUpdated
```

Jeżeli od podanej daty nie nastąpiła zmiana danych, backend zwraca:

```text
304 Not Modified
```

### Schedule

```text
GET    /api/schedule/{id}
POST   /api/schedule
DELETE /api/schedule/{id}
```

Endpoint pobierający harmonogram również obsługuje opcjonalny parametr `lastUpdated`.

## WebSocket

Połączenie WebSocket jest dostępne pod adresem:

```text
/api/ws
```

Backend wysyła komunikaty w formacie:

```json
{
  "type": "crew",
  "payload": {}
}
```

Pole `type` określa rodzaj aktualizowanych danych, a `payload` zawiera aktualny stan danych.

Przykładowe typy wiadomości:

```text
crew
schedule-<TRAINING_PATH>
```

## Baza danych

Projekt korzysta z PostgreSQL oraz Spring Data JPA.

Aktualne ustawienia Hibernate:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

`ddl-auto=update` automatycznie aktualizuje schemat bazy na podstawie encji JPA.

## SMSAPI

Backend posiada integrację z SMSAPI.

Aktualna konfiguracja:

```properties
smsapi.url=https://api.smsapi.pl/sms.do
smsapi.from=Test
smsapi.test=true
```

Token jest pobierany ze zmiennej środowiskowej:

```text
SMSAPI_TOKEN
```

W aktualnej konfiguracji SMSAPI działa w trybie testowym.

## Azure App Service

Produkcja jest uruchamiana w Azure App Service.

Sekrety i ustawienia środowiskowe należy skonfigurować w:

```text
App Service
→ Settings
→ Environment variables
```

W Azure powinny znajdować się:

```text
DB_URL
DB_USER
DB_PASSWORD
SMSAPI_TOKEN
FIREBASE_SERVICE_ACCOUNT_BASE64
```

Azure nie tworzy pliku `.env`. Wartości są przekazywane bezpośrednio do procesu aplikacji jako zmienne środowiskowe, które Spring Boot odczytuje przy starcie.

## Bezpieczeństwo

Repozytorium nie powinno zawierać:

```text
.env
firebase-service-account.json
prawdziwych haseł
tokenów API
kluczy prywatnych
```

Przed wdrożeniem produkcyjnym należy dodatkowo zweryfikować:

- konfigurację CORS,
- zakres publicznych endpointów `GET`,
- ustawienie `spring.jpa.show-sql`,
- ustawienie `spring.jpa.hibernate.ddl-auto`,
- tryb testowy SMSAPI.

## Pliki ignorowane przez Git

Repozytorium ignoruje m.in.:

```text
target/
out/
.idea/
.env
firebase-service-account.json
*.log
```

Do repozytorium powinien trafiać jedynie przykładowy plik:

```text
.env.example
```

bez prawdziwych sekretów.