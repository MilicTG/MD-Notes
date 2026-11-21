# MD Notes — Projektna Specifikacija

## 1. Vizija i cilj

**MD Notes** je jednostavna, ali konkretna *Markdown Notes* aplikacija s podrškom za slike, napravljena primarno za **učenje cijelog modernog stacka iz nule** i vraćanje *joy of programming* osjećaja.

Sav aplikacijski kod piše autor ručno; dokumentacija i specifikacije u ovom folderu služe kao mapa puta, ugovor između komponenti i referenca tijekom rada.

---

## 2. Tehnološki Stack

Projekt se sastoji od **dva identična API backenda** (radi direktne usporedbe i učenja oba pristupa) te **dvije nativne mobilne aplikacije**:

| Komponenta | Tehnologija | Napomena |
| :--- | :--- | :--- |
| **Backend A** | **Kotlin + Ktor** | Usporedba DX-a, korutina, serijalizacije i strukture projekta |
| **Backend B** | **Go (Golang)** | Usporedba jednostavnosti, standardne biblioteke / routera i performansi |
| **Android App** | **Android Native (Kotlin)** | Nativna klijentska aplikacija koja komunicira s bilo kojim od dva backenda |
| **iOS App** | **iOS Native (Swift)** | Nativna klijentska aplikacija koja komunicira s bilo kojim od dva backenda |

> **Ključna ideja:** Oba backenda implementiraju **isti REST API ugovor** (*API Contract*). To znači da Android i iOS aplikacije mogu raditi s Ktor backendom ili Go backendom jednostavnom promjenom `BASE_URL`-a.

---

## 3. Funkcionalni zahtjevi (MVP)

### Bilješke (Notes — CRUD)
- **Create:** Kreiranje nove bilješke (naslov + Markdown sadržaj).
- **Read:** Dohvat liste svih bilješki i dohvat pojedinačne bilješke po ID-u.
- **Update:** Uređivanje naslova i Markdown sadržaja postojeće bilješke.
- **Delete:** Brisanje bilješke.

### Markdown podrška
- Spremanje sadržaja kao plain-text Markdown (`.md` sintaksa: naslovi, liste, bold/italic, code blokovi, linkovi, slike).
- Prikaz (rendering) Markdowna u mobilnim aplikacijama (npr. *Edit* / *Preview* mod).

### Podrška za slike (Images)
- Upload slike s mobilnog uređaja na backend (npr. `multipart/form-data`).
- Backend sprema sliku (lokalni disk / storage) i vraća URL ili referencu.
- Slika se ubacuje u Markdown sadržaj bilješke standardnom sintaksom: `![opis](url_slike)`.
- Serviranje spremljenih slika preko HTTP-a tako da ih Android i iOS mogu prikazati unutar Markdown prikaza.

---

## 4. Predložena struktura repozitorija

```text
MD Notes/
├── ANTIGRAVITY.md          # Pravila rada mentora
├── spec/                   # Sva dokumentacija i specifikacije
│   ├── README.md           # Ovaj dokument (pregled projekta)
│   ├── data-model.md       # Model podataka, SQLite shema i spremanje slika
│   ├── api-contract.md     # Točna definicija REST endpointa, JSON payloada i status kodova
│   ├── backend-ktor.md     # (TODO) Bilješke i smjernice za Ktor implementaciju
│   ├── backend-go.md       # (TODO) Bilješke i smjernice za Go implementaciju
│   ├── android.md          # (TODO) Arhitektura i ekrani Android aplikacije
│   └── ios.md              # (TODO) Arhitektura i ekrani iOS aplikacije
├── backend-ktor/           # Ktor projekt (port 8080, piše autor)
├── backend-go/             # Go projekt (port 8081, piše autor)
├── android/                # Android Studio projekt (piše autor)
└── ios/                    # Xcode projekt (piše autor)
```

---

## 5. Odluke za MVP

- **Baza podataka:** SQLite (lokalna datoteka po backendu, bez dodatne infrastrukture).
- **Autentifikacija:** Nema za MVP (fokus na čisti CRUD, Markdown i slike).
- **Način rada na backendu:** **Paralelno Ktor + Go** — svaki milestone implementira se u oba jezika radi direktne usporedbe koncepata, sintakse i ergonomije.

---

## 6. Roadmap (Paralelni Milestoneovi)

### Faza 1 — Paralelni Backend (Ktor `:8080` + Go `:8081`)
- **M1 — Skeleton & Health Check:** Inicijalizacija projekata, JSON serijalizacija i `GET /health`.
- **M2 — SQLite & Create/Read Notes:** Spajanje na SQLite, kreiranje tablice `notes`, `POST /api/notes`, `GET /api/notes`, `GET /api/notes/{id}`.
- **M3 — Update, Delete & Validacija:** `PUT /api/notes/{id}`, `DELETE /api/notes/{id}`, validacija i standardizirani error odgovori.
- **M4 — Images Upload & Serving:** `POST /api/images` (`multipart/form-data`), spremanje na disk, validacija tipa/veličine, `GET /api/images/{id}` i `DELETE /api/images/{id}`.

### Faza 2 — Mobilne aplikacije (Android Native & iOS Native)
- **M5 — Specifikacija ekrana i networking sloja** (`spec/android.md`, `spec/ios.md`).
- **M6 — Lista bilješki i CRUD** (s mogućnošću prebacivanja između Ktor i Go servera).
- **M7 — Markdown editor/preview i upload slika**.
