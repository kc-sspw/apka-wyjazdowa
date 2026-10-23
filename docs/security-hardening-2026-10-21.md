# Poligon — roboczy przegląd higieny sekretów (2026-10-21)

**Status:** poprawki na osobnej gałęzi do review. Repozytorium Poligon jest tylko referencją dla głównej, prywatnej `kc-sspw/appka-eventowa`; ten PR **nie zatwierdza** ponownego użycia kodu ani uruchomienia usługi.

## Co zmieniono

- `docker-compose.yml:40` — literal hasła lokalnej bazy zastąpiono wymaganym `POSTGRES_PASSWORD` pochodzącym ze zmiennych środowiskowych lub niezarejestrowanego pliku `.env`. Bez tej wartości uruchomienie Compose powinno zakończyć się błędem konfiguracji zamiast użyć publicznego hasła.
- `compose/app/Dockerfile:5` — usunięto `COPY .env .`, aby nie umieszczać zawartości `.env` w warstwie build.
- `.dockerignore` — wyłączono z kontekstu Docker m.in. `.env`, pliki kont serwisowych, klucze i pliki seed mogące zawierać dane osobowe.
- `src/main/java/me/havooc/poligon/service/VerifiedUsersService.java:20-26,95-101` — usunięto wypisywanie adresów e-mail użytkowników, całej listy dozwolonych adresów oraz stacktrace z logów.

## Czego to nie naprawia

- `SecurityConfig.java:28-32` dopuszcza publiczne GET-y;
- `CrewController.java:26-59,96-100` nie rozróżnia ról przy operacjach administracyjnych;
- `WebSocketConfig.java:19-23` i `WebSocketHandler.java:29-56` pozwalają na nieizolowany globalny broadcast;
- `Crew.java:22-34` zawiera dane osobowe i flagę administratora;
- brak gwarancji, że historyczne obrazy Docker, cache, logi lub już utworzone bazy nie zawierają wcześniejszych sekretów.

Przed merge autor musi sprawdzić lokalnie `docker compose config` i build obrazu z fikcyjnym, silnym hasłem, zachowanie bazy, oraz skutki usunięcia logów. **Nie używać produkcyjnych sekretów do testów.**

## Obowiązek operatora

Ustalić, czy literal hasła z historii był wykorzystywany poza lokalnym demo. Jeśli tak, niezwłocznie obrócić hasło w faktycznej bazie i powiązanych usługach oraz skontrolować dostępność historycznych buildów, logów i poświadczeń. Usunięcie źródła nie unieważnia sekretu. Nie przepisujemy historii bez osobnej zgody.

Brak dynamicznych testów środowiska w ramach tego audytu.
