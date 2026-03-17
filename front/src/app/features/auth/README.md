# Auth

## Librairies et technologies

| Technologie | Usage dans le module auth |
|-------------|----------------------------|
| **Angular 20** | Core, composants standalone, signals, inject(), ChangeDetectionStrategy.OnPush |
| **@angular/router** | Guards (CanActivateFn, CanActivateChildFn), Router, redirections (/login, /feed) |
| **@angular/common/http** | HttpClient, HttpInterceptorFn, HttpContextToken, HttpErrorResponse |
| **@angular/forms** | ReactiveFormsModule, FormBuilder, Validators (login, register) |
| **@angular/core/rxjs-interop** | toObservable(), takeUntilDestroyed() |
| **Angular Material** | MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule (UI login/register/layout) |
| **RxJS** | Observable, catchError, switchMap, map, tap, finalize, shareReplay, filter, take |
| **TypeScript** | Typage des modèles (LoginRequest, TokenResponse, etc.) |

*(Versions : voir `package.json` à la racine du projet front.)*

---

## Résumé du flux

1. **Au démarrage** : l’app appelle `AuthFacade.bootstrap()` (csrf puis refresh). Le store retient « connecté » ou non ; `initialized` passe à true à la fin (même en erreur).

2. **Guards** : après `initialized`, ils lisent `store.isAuthenticated()`.
   - **authGuard** / **authChildGuard** : accès réservé aux connectés → sinon redirection `/login`.
   - **publicOnlyGuard** : accès réservé aux non-connectés (login, register) → sinon redirection `/feed`.

3. **Interceptor** : sur une réponse **401**, tente un **refresh** (une seule fois pour éviter les boucles), puis rejoue la requête. Si le refresh échoue → redirection `/login`.

4. **Qui fait quoi**
   - **AuthStore** : état (connecté oui/non, init faite).
   - **AuthApiService** : appels HTTP `/api/auth/*`.
   - **AuthFacade** : bootstrap, login, logout, refresh (utilisé par l’interceptor).
