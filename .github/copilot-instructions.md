# Copilot instructions for this repo

## Big picture
- Monorepo with three apps:
  - Backend: Spring Boot API in [backend/](backend/) (JWT auth, JPA, PostgreSQL).
  - Storefront: Nuxt 3 app in [frontend/](frontend/) using Pinia + localStorage persistence.
  - Admin (middle): Vue 3 + Vite + Naive UI in [middle/](middle/) with mock APIs.
- API flow: HTTP → `Controller` → `Service` → `Repository` → DB, returning `ApiResponse`/`PageResponse` DTOs; see [backend/PROJECT_STRUCTURE.md](backend/PROJECT_STRUCTURE.md).
- Frontend data flow relies on Pinia stores and localStorage; reference [frontend/ARCHITECTURE.md](frontend/ARCHITECTURE.md).
- Admin app uses mock endpoints mirroring backend routes (e.g. `/api/commerce/products`); mock data in [middle/mock/commerce/products.ts](middle/mock/commerce/products.ts).

## Critical workflows
- Backend:
  - Start (recommended): Windows `start.bat` or Linux `start.sh`; see [backend/QUICK_START.md](backend/QUICK_START.md).
  - DB: PostgreSQL 18; default connection in [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties).
  - API base URL: `http://localhost:8080/api`.
- Storefront (Nuxt): run `pnpm dev` in [frontend/](frontend/) (default `http://localhost:3000`).
- Admin (middle): run `pnpm dev` in [middle/](middle/) (mock APIs enabled by default; see [middle/README.md](middle/README.md)).

## Project-specific conventions
- Backend responses always wrap data in `ApiResponse` and paging in `PageResponse` (see [backend/src/main/java/io/cjc/backend/common/ApiResponse.java](backend/src/main/java/io/cjc/backend/common/ApiResponse.java)).
- Security/roles: `ADMIN`, `MERCHANT`, `USER`; JWT token required on protected routes (see [backend/src/main/java/io/cjc/backend/security/JwtTokenProvider.java](backend/src/main/java/io/cjc/backend/security/JwtTokenProvider.java)).
- Merchant data isolation is enforced via `merchantId` in JWT (documented in [backend/README.md](backend/README.md)).
- Frontend stores persist `cart`, `favorites`, `user`, `orders` to localStorage; initialize in `useProductStore()`/`useUserStore()` (see [frontend/stores/product.ts](frontend/stores/product.ts) and [frontend/stores/user.ts](frontend/stores/user.ts)).

## Integration points
- Frontend expects backend routes like `/api/auth/login`, `/api/commerce/products`, `/api/commerce/orders` (see [backend/README.md](backend/README.md)).
- Admin mock endpoints mirror backend paths; keep payload shape aligned when switching to real API (see [middle/README.md](middle/README.md)).

## When editing
- Keep backend layering (Controller → Service → Repository) and DTO usage; avoid leaking entity fields directly.
- Follow existing enums for status fields (`ProductStatus`, `OrderStatus`, etc.) in [backend/src/main/java/io/cjc/backend/enums/](backend/src/main/java/io/cjc/backend/enums/).
