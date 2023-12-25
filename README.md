# Szavazás alkalmazás

Az alkalmazás a parlamenti törvényhozói munka támogatását végzi. Többek között a
szavazások adatainak rögzítését és lekérdezhetőségét biztosítja. 

## Tartalomjegyzék

- [Jellemzők](#jellemzők)
- [Útmutató](#útmutató)
    - [Feltételek](#feltételek)
    - [Installálás](#installálás)
- [Használat](#használat)
- [Felhasznált technológiák](#felhasznált-technológiák)
- [Tesztek](#tesztek)
- [Licensz](#licensz)

## Jellemzők

- Szavazás adatainak elmentése: Szavazás elmentése json-ként
- Egy képviselő adott szavazáson leadott szavazatának lekérdezése
- A szavazás eredményének (elfogadott/elutasított) kiszámolása
- Adott napra a szavazások és eredményeik lekérdezése
- Átlag számítása: egy adott időszakon belül egy képviselő átlagosan hány szavazáson vett részt 

## Útmutató

### Feltételek

- Docker

### Installálás

1. Repository klónozása a következő címről:
    https://github.com/Aronrepo/szavazas

2. Image buildelése
    - Docker elindítása
    - A létrejött új könytárat bash terminállal meg kell nyitni és a következő parancsot kell kiadni:
    ```sh
    bash szavazas-docker_build.sh

3. Image futtatása
    - Az újonnan létrejött image-et a következő bash paranccsal lehet futtatni
    ```sh
    bash szavazas-docker_run.sh

## Használat

### API Endpointok

Postman dokumentáció: https://documenter.getpostman.com/view/25488726/2s9Ykt4JoW


## Felhasznált technológiák

- Java
- Spring Boot
- H2 Adatbázis

## Tesztek

Egyedül a SzavazasService ujSzavazas() metódusához készült két unit teszt

### Licensz

Ez a project az [MIT License](LICENSE) szerint van licenszelve.