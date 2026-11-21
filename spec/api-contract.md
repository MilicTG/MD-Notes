# REST API Contract — MD Notes

Ovaj dokument je **jedini izvor istine** (*Single Source of Truth*) za komunikaciju između mobilnih aplikacija (Android, iOS) i oba backenda (Ktor, Go).

Oba backenda moraju vraćati **identične rute, status kodove i JSON strukture**.

---

## 1. Opće konvencije

- **Portovi (za paralelni rad):**
  - Ktor backend: `http://localhost:8080`
  - Go backend: `http://localhost:8081`
- **Format podataka:** `application/json` (osim za upload i dohvat slika).
- **Imenovanje JSON polja:** `camelCase` (`createdAt`, `updatedAt`, `mimeType`, `sizeBytes`).
- **Datumi:** ISO-8601 UTC string (`2026-11-21T18:30:00Z`).
- **Autentifikacija:** Nema u MVP-u.

### Standardni format greške
Svaki odgovor s `4xx` ili `5xx` statusom mora vratiti JSON u ovom obliku:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Title must not be blank"
  }
}
```

Preporučeni `code` identifikatori:
- `VALIDATION_ERROR` (`400 Bad Request`)
- `NOT_FOUND` (`404 Not Found`)
- `UNSUPPORTED_MEDIA_TYPE` (`415 Unsupported Media Type`)
- `PAYLOAD_TOO_LARGE` (`413 Payload Too Large`)
- `INTERNAL_ERROR` (`500 Internal Server Error`)

---

## 2. Health Check

### `GET /health`
Provjera radi li server.

**Response `200 OK`:**
```json
{
  "status": "ok",
  "service": "md-notes-ktor"
}
```
*(Go backend vraća `"service": "md-notes-go"` — jedina razlika, korisna da u aplikaciji odmah znaš na koji si backend spojen.)*

---

## 3. Notes Endpointi

### 3.1. Lista svih bilješki — `GET /api/notes`
Vraća sve bilješke sortirane po `updatedAt` silazno (najnovije prve).

**Response `200 OK`:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Moja prva bilješka",
    "content": "# Naslov\n\nOvo je **Markdown** tekst.",
    "createdAt": "2026-11-21T18:30:00Z",
    "updatedAt": "2026-11-21T18:35:00Z"
  }
]
```
*(Ako nema bilješki, vraća prazan niz `[]`, nikad `null`.)*

---

### 3.2. Dohvat jedne bilješke — `GET /api/notes/{id}`

**Response `200 OK`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Moja prva bilješka",
  "content": "# Naslov\n\nOvo je **Markdown** tekst.",
  "createdAt": "2026-11-21T18:30:00Z",
  "updatedAt": "2026-11-21T18:35:00Z"
}
```

**Greške:**
- `404 Not Found` ako bilješka s tim `id`-em ne postoji.

---

### 3.3. Kreiranje bilješke — `POST /api/notes`

**Request Body:**
```json
{
  "title": "Moja prva bilješka",
  "content": "# Naslov\n\nTekst..."
}
```

**Response `201 Created`:**
Vraća kreirani `Note` objekt (s generiranim `id`, `createdAt`, `updatedAt`).

**Greške:**
- `400 Bad Request` ako je `title` prazan, predugačak (> 200 znakova) ili JSON nije ispravan.

---

### 3.4. Ažuriranje bilješke — `PUT /api/notes/{id}`

**Request Body:**
```json
{
  "title": "Ažurirani naslov",
  "content": "# Novi sadržaj\n\n![slika](/api/images/abc-123)"
}
```

**Response `200 OK`:**
Vraća ažurirani `Note` objekt (s osvježenim `updatedAt`).

**Greške:**
- `400 Bad Request` ako su podaci neispravni.
- `404 Not Found` ako bilješka ne postoji.

---

### 3.5. Brisanje bilješke — `DELETE /api/notes/{id}`

**Response `204 No Content`** (prazno tijelo odgovora).

**Greške:**
- `404 Not Found` ako bilješka ne postoji.

---

## 4. Images Endpointi

### 4.1. Upload slike — `POST /api/images`

**Request:**
- `Content-Type: multipart/form-data`
- Form field: `file` (binarni sadržaj slike)

**Response `201 Created`:**
```json
{
  "id": "7f3c9a12-1b4d-4e8a-9c21-8f0a2b3c4d5e",
  "url": "/api/images/7f3c9a12-1b4d-4e8a-9c21-8f0a2b3c4d5e",
  "mimeType": "image/png",
  "sizeBytes": 148230,
  "createdAt": "2026-11-21T18:40:00Z"
}
```

**Greške:**
- `400 Bad Request` ako nedostaje `file` polje.
- `413 Payload Too Large` ako je slika veća od 5 MB.
- `415 Unsupported Media Type` ako nije `image/jpeg`, `image/png` ili `image/webp`.

---

### 4.2. Dohvat slike — `GET /api/images/{id}`

**Response `200 OK`:**
- `Content-Type`: spremljeni `mimeType` slike (npr. `image/png`)
- Body: sirovi bajtovi slike

**Greške:**
- `404 Not Found` ako slika ne postoji (u bazi ili na disku).

---

### 4.3. Brisanje slike — `DELETE /api/images/{id}`

Briše zapis iz baze i datoteku s diska.

**Response `204 No Content`**.

**Greške:**
- `404 Not Found` ako slika ne postoji.

---

## 5. Kriterij završetka (Parity Check)

Za svaki milestone, oba backenda (`:8080` i `:8081`) moraju proći iste ručne testove (npr. preko `curl` ili HTTP klijenta):
1. `GET /health` vraća `200` i odgovarajući `service` naziv.
2. `POST /api/notes` kreira bilješku i vraća `201`.
3. `GET /api/notes` vraća niz s tom bilješkom.
4. `PUT /api/notes/{id}` mijenja sadržaj i `updatedAt`.
5. `DELETE /api/notes/{id}` vraća `204`, a ponovni `GET` vraća `404`.
6. `POST /api/images` sprema sliku i vraća `url`, a `GET /api/images/{id}` vraća istu sliku s ispravnim `Content-Type` headerom.
7. Neispravan unos (prazan `title`, prevelika slika, krivi format) vraća dogovoreni JSON error format.
