# MD Notes — Projektna Specifikacija

## 1. Vizija i cilj

**MD Notes** je jednostavna, ali konkretna *Markdown Notes* aplikacija s podrškom za slike, napravljena primarno za **učenje modernog mobile + backend stacka iz nule** i vraćanje *joy of programming* osjećaja.

Sav aplikacijski kod piše autor ručno; dokumentacija i specifikacije u ovom folderu služe kao mapa puta, ugovor između komponenti i referenca tijekom rada.

---

## 2. Tehnološki Stack

| Komponenta | Tehnologija | Napomena |
| :--- | :--- | :--- |
| **Backend** | **Kotlin + Ktor + Exposed + SQLite** | REST API za bilješke i slike (port `8080`) |
| **Android App** | **Android Native (Kotlin)** | Nativna klijentska aplikacija (dijeli jezik s backendom) |
| **iOS App** | **iOS Native (Swift)** | Nativna klijentska aplikacija |

> **Fokus:** Kotlin na backendu (Ktor) i Androidu + učenje Swifta na iOS-u. Pošto je REST API definiran u [api-contract.md](api-contract.md), u budućnosti se po želji uvijek može dodati i alternativni backend (npr. Go) bez promjena u mobilnim aplikacijama.

---

## 3. Funkcionalni zahtjevi (MVP)

### Bilješke (Notes — CRUD)
- **Create:** Kreiranje nove bilješke (naslov + Markdown sadržaj).
- **Read:** Dohvat liste svih bilješki i dohvat pojedinačne bilješke po ID-u.
- **Update:** Uređivanje naslova i Markdown sadržaja postojeće bilješke.
- **Delete:** Brisanje bilješke.

### Markdown podrška
- Spremanje sadržaja kao plain-text Markdown (`.md` sintaksa: naslovi, liste, bold/italic, code blokovi, linkovi, slike).
- Prikaz (rendering) Markdowna u mobilnim aplikacijama (*Edit* / *Preview* mod).

### Podrška za slike (Images)
- Upload slike s mobilnog uređaja na backend (`multipart/form-data`).
- Backend sprema sliku na lokalni disk (`data/uploads/`) i vraća URL.
- Slika se ubacuje u Markdown sadržaj bilješke standardnom sintaksom: `![opis](url_slike)`.
- Serviranje spremljenih slika preko HTTP-a tako da ih Android i iOS mogu prikazati unutar Markdown prikaza.

---

## 4. Struktura repozitorija

```text
MD Notes/
├── ANTIGRAVITY.md          # Pravila rada mentora
├── spec/                   # Sva dokumentacija i specifikacije
│   ├── README.md           # Ovaj dokument (pregled projekta)
│   ├── data-model.md       # Model podataka, SQLite shema i spremanje slika
│   ├── api-contract.md     # Točna definicija REST endpointa, JSON payloada i status kodova
│   ├── android.md          # (TODO) Arhitektura i ekrani Android aplikacije
│   └── ios.md              # (TODO) Arhitektura i ekrani iOS aplikacije
├── backend-ktor/           # Ktor backend (port 8080, piše autor)
├── android/                # Android Studio projekt (piše autor)
└── ios/                    # Xcode projekt (piše autor)
```

---

## 5. Odluke za MVP

- **Baza podataka:** SQLite (lokalna datoteka `backend-ktor/data/notes.db`, bez dodatne infrastrukture) + **JetBrains Exposed DSL**.
- **Autentifikacija:** Nema za MVP (fokus na čisti CRUD, Markdown i slike).
- **Arhitektura koda:** Feature-based organizacija (`health/`, `notes/`, `images/`).

---

## 6. Roadmap (Milestoneovi)

### Faza 1 — Ktor Backend (`backend-ktor/`, port `8080`)
- **M1 — Skeleton & Health Check:** ✅ Inicijalizacija projekta, JSON serijalizacija i `GET /health`.
- **M2 — SQLite (Exposed) & Create/Read Notes:** ✅ Spajanje na SQLite, tablica `notes`, `POST /api/notes`, `GET /api/notes`, `GET /api/notes/{id}`.
- **M3 — Update, Delete & Validacija:** `PUT /api/notes/{id}`, `DELETE /api/notes/{id}`, validacija i standardizirani error odgovori (`StatusPages`).
- **M4 — Images Upload & Serving:** `POST /api/images` (`multipart/form-data`), spremanje na disk, validacija tipa/veličine, `GET /api/images/{id}` i `DELETE /api/images/{id}`.

### Faza 2 — Mobilne aplikacije (Android Native & iOS Native)
- **M5 — Specifikacija ekrana i networking sloja** (`spec/android.md`, `spec/ios.md`).
- **M6 — Lista bilješki i CRUD** na Androidu i iOS-u.
- **M7 — Markdown editor/preview i upload slika**.
