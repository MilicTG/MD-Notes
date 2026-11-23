# Data Model & Storage — MD Notes

Ova specifikacija definira strukturu podataka i način spremanja za Ktor backend (SQLite + JetBrains Exposed DSL).

---

## 1. Baza podataka: SQLite

- **Baza podataka:** `backend-ktor/data/notes.db`
- **Slike na disku:** lokalni folder `backend-ktor/data/uploads/` (ne spremamo binarne slike u SQLite `BLOB`, već samo metapodatke u bazu, a samu datoteku na disk).

> **Zašto slike na disk, a ne u bazu?**
> Jednostavnije serviranje preko HTTP-a, manja baza podataka i realan obrazac koji se kasnije lako zamijeni s S3 / Cloud Storageom.

---

## 2. Tablica: `notes`

Predstavlja jednu Markdown bilješku.

| Kolona | SQLite tip | Ograničenja | Opis |
| :--- | :--- | :--- | :--- |
| `id` | `TEXT` | `PRIMARY KEY` | UUID v4 string (npr. `550e8400-e29b-41d4-a716-446655440000`) |
| `title` | `TEXT` | `NOT NULL` | Naslov bilješke (ne smije biti prazan string) |
| `content` | `TEXT` | `NOT NULL DEFAULT ''` | Sirovi Markdown sadržaj (uključujući reference na slike) |
| `created_at` | `TEXT` | `NOT NULL` | ISO-8601 UTC timestamp (npr. `2026-11-21T18:30:00Z`) |
| `updated_at` | `TEXT` | `NOT NULL` | ISO-8601 UTC timestamp zadnje izmjene |

### Napomene za implementaciju
- `id` generira backend prilikom kreiranja bilješke.
- `created_at` i `updated_at` su jednaki kod kreiranja; kod updatea mijenja se samo `updated_at`.
- Zadano sortiranje liste bilješki: `ORDER BY updated_at DESC` (najnovije uređene prve).

---

## 3. Tablica: `images`

Čuva metapodatke o uploadanim slikama kako bi backend znao validirati, servirati (ispravan `Content-Type`) i obrisati datoteku s diska.

| Kolona | SQLite tip | Ograničenja | Opis |
| :--- | :--- | :--- | :--- |
| `id` | `TEXT` | `PRIMARY KEY` | UUID v4 string |
| `filename` | `TEXT` | `NOT NULL` | Ime datoteke na disku (npr. `<id>.png` — **nikad** ne koristiti sirovo korisničko ime datoteke za putanju na disku zbog sigurnosti!) |
| `original_name` | `TEXT` | `NOT NULL` | Originalno ime datoteke koje je klijent poslao (informativno) |
| `mime_type` | `TEXT` | `NOT NULL` | MIME tip (`image/jpeg`, `image/png`, `image/webp`) |
| `size_bytes` | `INTEGER` | `NOT NULL` | Veličina datoteke u bajtovima |
| `created_at` | `TEXT` | `NOT NULL` | ISO-8601 UTC timestamp |

### Veza između bilješki i slika (MVP pristup)
- U MVP-u slike nisu strogo vezane stranim ključem (`FOREIGN KEY`) za bilješku.
- Tok rada:
  1. Klijent uploada sliku na `POST /api/images`.
  2. Backend sprema sliku i vraća `id` i relativni `url` (npr. `/api/images/<id>`).
  3. Klijent ubacuje `![alt](/api/images/<id>)` u Markdown tekst bilješke i sprema bilješku.
- Ovo znatno pojednostavljuje UX (korisnik može dodati sliku i dok piše potpuno novu, još nespremljenu bilješku).

---

## 4. Validacijska pravila

### Bilješka (`Note`)
- `title`: obavezan, nakon `trim()` dužina između **1 i 200** znakova.
- `content`: smije biti prazan string `""`, preporučeni limit npr. **100 000** znakova.

### Slika (`Image`)
- Dozvoljeni MIME tipovi: `image/jpeg`, `image/png`, `image/webp`.
- Maksimalna veličina datoteke: **5 MB** (`5 * 1024 * 1024` bajtova).
- Ekstenzija na disku određuje se na temelju validiranog MIME tipa (`.jpg`, `.png`, `.webp`), ne slijepo iz korisničkog naziva datoteke.
