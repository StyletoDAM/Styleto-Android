# 📱 ANALYSE COMPLÈTE DE L'APPLICATION ANDROID LABASNI

## 🏗️ ARCHITECTURE GÉNÉRALE

**Framework:** Jetpack Compose  
**Langage:** Kotlin  
**Plateforme:** Android (minSdk 24, targetSdk 36)  
**Pattern:** MVVM (Model-View-ViewModel)  
**État:** Géré via `StateFlow`, `collectAsState()`, `remember`  
**Navigation:** Jetpack Navigation Compose  
**API:** REST avec Retrofit + OkHttp  
**WebSocket:** Socket.IO pour chat temps réel  
**Authentification:** JWT, Google Sign-In  
**Base de données locale:** SharedPreferences pour token/utilisateur  
**Image Loading:** Coil  

---

## 📂 STRUCTURE DE L'APPLICATION

### **POINT D'ENTRÉE**
- **`LabasniApp.kt`**
  - Point d'entrée principal avec navigation
  - Routes définies dans `LabasniDestination` (sealed class)
  - Navigation entre Intro, Login, Signup, Forgot Password, Home
  - Utilise `rememberNavController()` pour gérer la navigation

- **`MainActivity.kt`**
  - Activité principale
  - Initialise `ThemeController` au démarrage
  - Configure le thème via `LabasniTheme`
  - Affiche `LabasniApp()` comme contenu principal

---

## 🗂️ MODULES PRINCIPAUX

### **1. AUTHENTICATION (`ui/screen/auth/` + `models/repositories/` + `api/`)**

#### **Vues:**
- **`LoginView.kt`**
  - Formulaire de connexion (email/password)
  - Bouton OAuth Google
  - Navigation vers Signup et Forgot Password
  - Snackbar pour erreurs
  - Redirection vers Home après connexion réussie

- **`SignupView.kt`**
  - Formulaire d'inscription complet:
    - Full Name, Email, Phone Number (avec `PhoneInputField`)
    - Password avec validation
    - Gender (Male/Female) - chips visuels
  - Navigation vers vérification email

- **`ForgotPasswordView.kt`**
  - Demande de réinitialisation de mot de passe
  - OTP par SMS via Twilio

#### **ViewModels:**
- **`LoginViewModel.kt`**
  - Gère la connexion (email/password)
  - Appelle `AuthService.signin()`
  - Stocke le token via `TokenManager`
  - Gère Google Sign-In

- **`SignupViewModel.kt`**
  - Validation du formulaire d'inscription
  - Gestion du processus d'inscription

- **`ForgotPasswordViewModel.kt`**
  - Demande OTP
  - Vérification OTP
  - Réinitialisation du mot de passe

#### **Services:**
- **`AuthService.kt`**
  - Wrapper pour les appels API d'authentification
  - Méthodes: `signin()`, `signup()`, `googleAuth()`, etc.

#### **Repositories:**
- **`AuthRepository.kt`**
  - Gère les appels API via Retrofit
  - Stockage du token et userId dans `TokenManager`

#### **API:**
- **`AuthApi.kt`**
  - Interface Retrofit pour les endpoints auth
  - **Endpoints:**
    - `POST /auth/signup`
    - `POST /auth/signin`
    - `POST /auth/google`
    - `POST /auth/apple`
    - `GET /auth/profile`
    - `PATCH /auth/profile`
    - `PATCH /auth/profile/photo` (Multipart)
    - `POST /auth/verify-email`
    - `POST /auth/forgot-password`
    - `POST /auth/verify-otp`
    - `POST /auth/reset-password`
    - `DELETE /auth/profile`

---

### **2. DRESSING (`ui/screen/dressing/` + `models/repositories/` + `api/`)**

#### **Vues:**
- **`DressingView.kt`**
  - Vue principale du dressing (garderobe)
  - Grille de vêtements (2 colonnes avec `LazyVerticalGrid`)
  - Barre de recherche avec `OutlinedTextField`
  - Filtres par catégorie (chips): All, Tshirt, Pants, Dress, Shoes, Accessory
  - Bouton flottant "+" pour ajouter un vêtement
  - Cards avec bordure colorée selon catégorie
  - Dialog de suppression

- **`AIAnalysisLoadingScreen.kt`**
  - Écran de chargement plein écran pendant l'analyse IA
  - Animation de chargement

- **`DetectionResultBottomSheet.kt`**
  - BottomSheet de résultat de l'analyse IA
  - Affiche l'image depuis Cloudinary (URL)
  - Détails détectés (category, color, style, season)
  - Formulaire pour corrections (si détection incorrecte)
  - Bouton "Save to Dressing" → création du vêtement en base

- **`ImageSourceDialog.kt`**
  - Dialog pour choisir entre caméra et galerie

#### **ViewModels:**
- **`DressingViewModel.kt`**
  - **État:**
    - `clothes: StateFlow<List<Cloth>>` - Liste complète
    - `isLoading: StateFlow<Boolean>`
    - `isDetecting: StateFlow<Boolean>`
    - `detectionResult: StateFlow<Pair<DetectionResult, String>?>`
    - `isSaving: StateFlow<Boolean>`
    - `errorMessage: StateFlow<String?>`
    - `successMessage: StateFlow<String?>`
    - `deletingIds: StateFlow<Set<String>>`
  - **Méthodes:**
    - `loadClothes(token)` - Charge les vêtements depuis l'API
    - `detectCloth(bitmap)` - Détecte un vêtement via `/detect`
    - `saveDetectedCloth(...)` - Sauvegarde le vêtement détecté
    - `deleteCloth(token, id)` - Supprime un vêtement

#### **Repositories:**
- **`DressingRepository.kt`**
  - Gère les appels API pour les vêtements
  - Méthodes: `fetchMyClothes()`, `createCloth()`, `deleteCloth()`, `detectCloth()`

#### **API:**
- **`ClothesApi.kt`**
  - Interface Retrofit pour les endpoints clothes
  - **Endpoints:**
    - `GET /cloth/my` - Mes vêtements
    - `POST /cloth` - Créer un vêtement
    - `DELETE /cloth/:id` - Supprimer un vêtement
    - `POST /detect` (Multipart) - Détection IA

#### **Services:**
- **`CloudinaryGalleryService.kt`**
  - Gestion des images Cloudinary (galerie)

#### **Utils:**
- **`DetectionResultParser.kt`**
  - Parse le résultat de détection depuis le format JSON string
- **`ColorNameConverter.kt`**
  - Conversion des noms de couleurs

#### **Workflow d'ajout d'un vêtement:**
1. Utilisateur appuie sur "+" → `ImageSourceDialog`
2. Choix caméra ou galerie
3. Prise/sélection photo → Upload vers `/detect` (Multipart)
4. **Backend traite:**
   - Suppression du background (remove.bg API)
   - Upload image sans BG sur Cloudinary
   - Détection IA (YOLO + modèle style/saison)
5. Affichage du résultat dans `DetectionResultBottomSheet` (URL Cloudinary)
6. Utilisateur peut corriger les détections
7. "Save to Dressing" → Création du vêtement via `POST /cloth`

---

### **3. OUTFITS/TENUES (`ui/screen/tenues/` + `models/repositories/` + `api/`)**

#### **Vues:**
- **`TenuesView.kt`**
  - Vue principale des tenues
  - Bannière "Today's Suggestion" avec bouton "See suggestion"
  - Liste des tenues récentes (`TenueCard`)
  - Navigation vers `FavoriteTab`
  - Bouton favoris dans la toolbar
  - Style selection popup (ModalBottomSheet)

- **`FavoriteTab`**
  - Liste des outfits favoris
  - Empty state si aucun favori

#### **ViewModels:**
- **`TenuesViewModel.kt`**
  - **État:**
    - `outfits: StateFlow<List<Outfit>>`
    - `isLoading: StateFlow<Boolean>`
    - `errorMessage: StateFlow<String?>`
    - `successMessage: StateFlow<String?>`
  - **Méthodes:**
    - `initialize(token, userId)` - Initialise et charge les outfits
    - `loadOutfits(token)` - Charge les outfits depuis l'API
    - `toggleFavorite(outfitId)` - Bascule le favori

#### **Repositories:**
- **`TenuesRepository.kt`**
  - Gère les appels API pour les outfits
  - Méthodes: `fetchMyOutfits()`, `createOutfit()`, `deleteOutfit()`

#### **API:**
- **`OutfitsApi.kt`**
  - Interface Retrofit pour les endpoints outfits
  - **Endpoints:**
    - `GET /outfits/my` - Mes outfits
    - `POST /outfits` - Créer un outfit
    - `DELETE /outfits/:id` - Supprimer un outfit

---

### **4. AVATAR/MIRROR (`ui/screen/mirror/`)**

#### **Vues:**
- **`MirrorView.kt`**
  - Vue de try-on virtuel (miroir)
  - Utilise CameraX pour la caméra frontale
  - Preview en temps réel
  - (Fonctionnalité en développement)

---

### **5. STORE (`ui/screen/store/` + `models/repositories/` + `api/`)**

#### **Vues:**
- **`StoreView.kt`**
  - Vue principale de la boutique
  - Header avec boutons Messages et Panier (cercles colorés)
  - Barre de recherche
  - Section "My Items" (mes articles en vente)
  - Section "Discover" (articles des autres)
  - Grille de produits (2 colonnes)
  - Bouton flottant "+" pour ajouter un article
  - Sheets: `AddToStoreSheet`, `EditStoreDialog`
  - Toast notification pour suppression

- **`cart/CartView.kt`**
  - Panier d'achat
  - Liste des articles ajoutés
  - Total
  - Procédure de paiement (Stripe)

- **`messaging/MessagingView.kt`**
  - Liste des conversations
  - Navigation vers `ChatDetailView`

- **`messaging/ChatDetailView.kt`**
  - Conversation détaillée en temps réel
  - Messages en temps réel via WebSocket
  - Input bar pour envoyer des messages
  - Statut de connexion

#### **ViewModels:**
- **`StoreViewModel.kt`**
  - **État:**
    - `storeItems: StateFlow<List<StoreItem>>` - Mes articles
    - `discoverItems: StateFlow<List<StoreItem>>` - Articles des autres
    - `availableClothes: StateFlow<List<Cloth>>` - Vêtements disponibles pour vente
    - `searchText: StateFlow<String>`
    - `showAddToStore: StateFlow<Boolean>`
    - `showToast: StateFlow<Boolean>`
    - `isLoading: StateFlow<Boolean>`
    - `isLoadingClothes: StateFlow<Boolean>`
    - `isSubmitting: StateFlow<Boolean>`
    - `deletingIds: StateFlow<Set<String>>`
  - **Méthodes:**
    - `initialize(token, userId)` - Initialise et charge les données
    - `loadMyStore(token)` - Charge mes articles
    - `loadDiscoverItems(token)` - Charge les articles des autres
    - `loadAvailableClothes(token)` - Charge mes vêtements
    - `addStoreItem(...)` - Crée un article
    - `updateStorePrice(...)` - Met à jour le prix et la taille
    - `markAsSold(token, id)` - Marque comme vendu
    - `deleteStoreItem(token, id)` - Supprime un article

- **`CartViewModel.kt`**
  - Gère le panier d'achat

- **`ChatDetailViewModel.kt`**
  - Gère les messages en temps réel
  - WebSocket integration

- **`MessagingViewModel.kt`**
  - Gère la liste des conversations

#### **Repositories:**
- **`StoreRepository.kt`**
  - Gère les appels API pour la boutique
  - Méthodes: `fetchMyStore()`, `fetchAllStoreItems()`, `createStoreItem()`, `updateStoreItem()`, `deleteStoreItem()`

- **`ChatRepository.kt`**
  - Gère les appels API pour le chat
  - Méthodes: `createOrGetConversation()`, `fetchMyConversations()`, `fetchMessages()`

#### **API:**
- **`StoreApi.kt`**
  - Interface Retrofit pour les endpoints store
  - **Endpoints:**
    - `GET /store/my` - Mes articles
    - `GET /store` - Tous les articles
    - `POST /store` - Créer un article
    - `PATCH /store/:id` - Mettre à jour un article
    - `DELETE /store/:id` - Supprimer un article

- **`ChatApi.kt`**
  - Interface Retrofit pour les endpoints chat
  - **Endpoints:**
    - `POST /chat/conversations` - Créer/récupérer une conversation
    - `GET /chat/conversations` - Mes conversations
    - `GET /chat/conversations/:id/messages` - Messages d'une conversation
    - `POST /chat/messages` - Envoyer un message (REST fallback)

#### **WebSocket (Chat):**
- Utilise Socket.IO client (`io.socket:socket.io-client:2.0.1`)
- Configuration dans `ChatDetailViewModel`
- Événements: `connected`, `new-message`, `join-conversation`, `send-message`

---

### **6. PROFILE/SETTINGS (`ui/screen/profile/` + `ui/screen/settings/` + `models/repositories/`)**

#### **Vues:**
- **`ProfileView.kt`**
  - Affichage du profil utilisateur

- **`SettingsView.kt`**
  - Vue principale des paramètres
  - Sections expansibles (comme iOS):
    1. **Edit Profile** - Édition du profil (fullName, phone, gender, photo)
    2. **App Settings** - Notifications, Language, Font Size
    3. **Preferences** - Theme, Style préféré, Animations
    4. **Security** - Change Password, Delete Account
    5. **Help & Support** - Contact, FAQ, Version
  - Photo de profil avec option de modification/suppression (caméra/galerie)
  - Dialogs: ThemePicker, StyleTheme, ChangePassword, DeleteAccount

#### **ViewModels:**
- **`ProfileViewModel.kt`**
  - **État:**
    - `user: StateFlow<User?>`
    - `isLoading: StateFlow<Boolean>`
    - `isPhotoUpdating: StateFlow<Boolean>`
    - `errorMessage: StateFlow<String?>`
    - `successMessage: StateFlow<String?>`
    - `accountDeleted: StateFlow<Boolean>`
  - **Méthodes:**
    - `loadProfile(token)` - Charge le profil
    - `updateProfile(...)` - Met à jour le profil (texte)
    - `uploadProfilePhoto(token, imageBytes)` - Met à jour la photo (Multipart)
    - `setProfilePictureFromUrl(token, url)` - Supprime la photo (url vide)
    - `deleteAccount(token)` - Supprime le compte

#### **Repositories:**
- **`ProfileRepository.kt`**
  - Gère les appels API pour le profil
  - Méthodes: `fetchProfile()`, `updateProfile()`, `updateProfilePhoto()`, `deleteAccount()`

---

### **7. INTRO (`ui/screen/intro/`)**

#### **Vues:**
- **`IntroView.kt`**
  - Écran d'accueil (si non connecté)
  - Logo Labasni
  - Dégradé rose/aqua
  - Boutons "Log In" et "Sign Up"
  - Navigation vers les vues d'authentification

---

## 🛠️ UTILS ET HELPERS

### **`APIConstants.kt`**
- **Base URL:** `http://10.0.2.2:3000` (émulateur) ou `labasni.baseUrl` depuis `local.properties`
- Définit tous les endpoints API:
  - Auth paths
  - Clothes paths
  - Outfits paths
  - Store paths
  - Chat paths

### **`TokenManager.kt`**
- Gestion du token JWT et userId
- Stockage: SharedPreferences (`labasni_prefs`)
- **Méthodes:**
  - `saveToken(context, token)` - Sauvegarde le token
  - `getToken(context)` - Récupère le token
  - `clearToken(context)` - Supprime le token
  - `saveUserId(context, userId)` - Sauvegarde le userId
  - `getUserId(context)` - Récupère le userId
  - `saveGender(context, gender)` - Sauvegarde le genre
  - `getGender(context)` - Récupère le genre

### **`RetrofitClient.kt`**
- Configuration Retrofit centralisée
- **Instances:**
  - `authApi: AuthApi`
  - `clothesApi: ClothesApi`
  - `outfitsApi: OutfitsApi`
  - `storeApi: StoreApi`
  - `chatApi: ChatApi`
- **Configuration:**
  - Base URL depuis `APIConstants.BASE_URL`
  - Logging interceptor (BODY level)
  - Timeout: 30 secondes
  - Gson converter

### **`ThemeController.kt`**
- Gestion dynamique des thèmes
- **Modes:**
  - `LIGHT` - Thème clair forcé
  - `DARK` - Thème sombre forcé
  - `SYSTEM` - Suit les préférences système
- **Variants:**
  - `PINK` - Thème rose (Female)
  - `BLUE` - Thème bleu (Male)
- **Méthodes:**
  - `initialize(context)` - Initialise les préférences
  - `setThemeMode(mode)` - Change le mode
  - `setThemeVariant(variant)` - Change le variant
  - `syncThemeVariantWithGender(context, gender)` - Synchronise avec le genre
  - `syncThemeVariantWithSavedGender(context)` - Synchronise avec le genre sauvegardé

### **`Color.kt`** & **`Theme.kt`**
- Définit les couleurs du thème
- Couleurs dynamiques selon genre:
  - **Femme (PINK):** Primary = Rose, Secondary = Rose clair, Teal = Aqua
  - **Homme (BLUE):** Primary = Aqua, Secondary = Aqua clair, Teal = Rose
- `DynamicThemeColors` - Classe utilitaire pour obtenir les couleurs selon le genre

### **`CategoryColors.kt`**
- Mappage des couleurs par catégorie de vêtement
- Utilisé pour les bordures des cartes de vêtements

### **`ContextExtensions.kt`**
- Extensions pour `Context`
- `findActivity()` - Trouve l'Activity depuis un Context

### **`MediaPermissionUtils.kt`**
- Utilitaires pour les permissions média (caméra, stockage)

### **`NetworkError.kt`**
- Sealed class pour les erreurs réseau
- Types: `InvalidURL`, `Transport`, `ServerError`, `DecodingFailed`, `Unauthorized`, `RequestFailed`, `ServerMessage`, `NoData`

---

## 📊 MODÈLES DE DONNÉES

### **`models/entities/`**

#### **`User.kt`**
```kotlin
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val gender: Gender, // MALE, FEMALE
    val preferences: List<String>? = null,
    val phoneNumber: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val profilePicture: String? = null,
    val authProvider: String? = null
)
```

#### **`Cloth.kt`**
```kotlin
data class Cloth(
    val id: String,
    val imageUrl: String,
    val type: String, // Tshirt, Pants, Dress, Shoes, Accessory
    val name: String,
    val season: String? = null,
    val color: String? = null,
    val style: String? = null,
    val userId: String? = null
)
```

#### **`Outfit.kt`**
```kotlin
data class Outfit(
    val id: String,
    val clothes: List<Cloth>,
    val eventType: String? = null,
    val weatherType: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val isFavorite: Boolean = false
)
```

#### **`StoreItem.kt`**
```kotlin
data class StoreItem(
    val id: String,
    val cloth: Cloth?,
    val price: Double,
    val size: String?,
    val status: String?, // "available", "sold"
    val ownerId: String,
    val ownerName: String? = null,
    val ownerAvatar: String? = null
)
```

#### **`Conversation.kt`** & **`Message.kt`**
- Modèles pour le chat
- Conversation avec participants et messages

#### **`DetectionResult.kt`**
- Résultat de l'analyse IA
- Contient: category, color, style, season

#### **`Responses.kt`**
- DTOs pour les réponses API
- `SigninResponse`, `SignupResponse`, `ForgotPasswordResponse`, etc.

---

## 🧭 NAVIGATION

### **Structure de navigation principale:**

```
LabasniApp
├── IntroView (startDestination)
│   ├── LoginView
│   │   ├── SignupView
│   │   └── ForgotPasswordView
│   └── SignupView
│
└── MainScreen (Home)
    └── MainScreen (TabView)
        ├── DressingTab (Tab 1)
        │   ├── ImageSourceDialog
        │   ├── AIAnalysisLoadingScreen
        │   └── DetectionResultBottomSheet
        │
        ├── TenuesTab (Tab 2)
        │   ├── FavoriteTab
        │   └── StyleSelectionPopup
        │
        ├── MirrorView (Tab 3 - Central)
        │
        ├── StoreTab (Tab 4)
        │   ├── AddToStoreSheet
        │   ├── EditStoreDialog
        │   ├── CartView
        │   ├── MessagingView
        │   │   └── ChatDetailView
        │   └── ChatDetailView (direct)
        │
        └── SettingsView (Tab 5)
            ├── ThemePickerDialog
            ├── StyleThemeDialog
            ├── ChangePasswordDialog
            └── DeleteAccountDialog
```

### **MainScreen (HomeTabs):**
- TabBar personnalisée avec 5 onglets:
  1. **Dressing** (icône: Checkroom)
  2. **Tenues** (icône: People)
  3. **Avatar** (icône: AutoAwesome) - Bouton central flottant
  4. **Store** (icône: ShoppingBag)
  5. **Settings** (icône: Person)

---

## 🔐 GESTION DE L'AUTHENTIFICATION

### **Flow de connexion:**
1. L'utilisateur saisit email/password dans `LoginView`
2. `LoginViewModel` appelle `AuthService.signin()`
3. Si succès, le token JWT est stocké via `TokenManager.saveToken()`
4. Le userId est stocké via `TokenManager.saveUserId()`
5. Le genre est stocké via `TokenManager.saveGender()`
6. Redirection vers `MainScreen`

### **Flow de déconnexion:**
1. Utilisateur appuie sur "Logout" dans `SettingsView`
2. `TokenManager.clearToken()` - Supprime token, userId, gender
3. Redirection vers `LoginView`

### **Persistance:**
- Token JWT: SharedPreferences (`labasni_prefs`, key `access_token`)
- UserId: SharedPreferences (key `user_id`)
- Genre: SharedPreferences (key `gender`)

---

## 🎨 SYSTÈME DE THÈMES

### **Thèmes dynamiques:**
- Les couleurs s'adaptent selon le genre de l'utilisateur
- **Femme (PINK):** Palette rose dominante
- **Homme (BLUE):** Palette aqua/teal dominante

### **Couleurs:**
- `primary` - Couleur principale (adaptée au genre)
- `secondary` - Couleur secondaire
- `softPink` - Rose doux
- `aqua` - Aqua
- `teal` - Teal
- `background` - Fond
- `card` - Cartes
- `text` - Texte principal
- `secondaryText` - Texte secondaire

### **Modes:**
- `LIGHT` - Thème clair forcé
- `DARK` - Thème sombre forcé
- `SYSTEM` - Suit les préférences système

### **Synchronisation automatique:**
- Le thème se synchronise automatiquement avec le genre lors du chargement du profil
- Utilise `ThemeController.syncThemeVariantWithGender()`

---

## 📡 INTÉGRATION API

### **Base URL:**
- `http://10.0.2.2:3000` (émulateur Android)
- Configurable via `local.properties`: `labasni.baseUrl=http://<ip>:3000` (téléphone réel)
- Lue depuis `BuildConfig.BASE_URL` (généré depuis `build.gradle.kts`)

### **Authentification:**
- Toutes les requêtes protégées incluent: `Authorization: Bearer <token>`
- Token récupéré via `TokenManager.getToken(context)`

### **Méthodes HTTP utilisées:**
- `GET` - Récupération de données
- `POST` - Création
- `PATCH` - Mise à jour partielle
- `DELETE` - Suppression
- `Multipart` - Upload de fichiers (images)

### **Gestion des erreurs:**
- `NetworkError` sealed class pour typage des erreurs
- Messages d'erreur affichés via Snackbar

---

## 🔄 WEBSocket (Chat temps réel)

### **Configuration:**
- Utilise Socket.IO client (`io.socket:socket.io-client:2.0.1`)
- Configuration dans `ChatDetailViewModel`
- Authentification: JWT dans headers

### **Événements:**
- `connected` - Confirmation de connexion serveur
- `new-message` - Nouveau message reçu
- `join-conversation` - Rejoindre une room
- `send-message` - Envoyer un message

### **Fallback REST:**
- Si WebSocket déconnecté, envoi via `POST /chat/messages`

---

## 💾 PERSISTANCE LOCALE

### **SharedPreferences:**
- Token JWT (`access_token`)
- UserId (`user_id`)
- Genre (`gender`)
- Mode thème (`theme_mode`)
- Variant thème (`theme_variant`)

---

## 📦 SERVICES EXTERNES INTÉGRÉS

### **Cloudinary:**
- Stockage d'images (vêtements, photos de profil)
- URLs fournies par le backend
- Utilisé pour afficher les images via Coil

### **Stripe:**
- Paiements (payment intents)
- Intégration côté backend

### **Twilio:**
- Envoi de SMS/OTP
- Intégration côté backend

### **Google Sign-In:**
- Authentification OAuth Google
- SDK: `com.google.android.gms:play-services-auth`

### **CameraX:**
- Capture vidéo pour le miroir
- SDK: `androidx.camera:camera-*`

### **Coil:**
- Chargement d'images asynchrone
- SDK: `io.coil-kt:coil-compose`

---

## 🔧 FICHIERS DE CONFIGURATION

### **`AndroidManifest.xml`**
- Configuration de l'app
- Permissions: CAMERA, READ_EXTERNAL_STORAGE, INTERNET
- FileProvider pour partager des fichiers avec la caméra

### **`build.gradle.kts`**
- Configuration du projet
- Dépendances: Compose, Retrofit, OkHttp, Coroutines, ViewModel, etc.

### **`local.properties`**
- Configuration locale
- `labasni.baseUrl` pour l'URL du backend (optionnel)

---

## 📈 FONCTIONNALITÉS AVANCÉES

### **1. Détection IA des vêtements:**
- Upload image vers `/detect` (Multipart)
- Backend fait:
  - Suppression de background (remove.bg)
  - Détection YOLO
  - Classification style/saison (TensorFlow)
  - Upload sur Cloudinary
- Résultat affiché avec possibilité de correction

### **2. Génération d'outfits:**
- UI prête, mais logique de génération non implémentée
- Bouton "See suggestion" ouvre le style popup

### **3. Chat temps réel:**
- WebSocket pour messages instantanés
- Fallback REST si déconnecté
- Statut de connexion

### **4. Thèmes dynamiques:**
- Adaptation selon genre utilisateur
- Support light/dark mode
- Changement en temps réel

### **5. Recherche et filtres:**
- Recherche textuelle (vêtements, store)
- Filtres par catégorie
- Debounce pour performance

### **6. Upload d'images:**
- Support caméra et galerie
- Compression d'images pour éviter OutOfMemoryError
- Utilisation de FileProvider pour la caméra

---

## 🐛 GESTION DES ERREURS

### **Types d'erreurs:**
- **NetworkError:**
  - `InvalidURL`
  - `Transport(Throwable)`
  - `ServerError`
  - `DecodingFailed`
  - `Unauthorized`
  - `RequestFailed(Int)`
  - `ServerMessage(String)`
  - `NoData`

### **Affichage:**
- **Snackbar** (bas de l'écran) - Pour erreurs temporaires
- **AlertDialog** - Pour erreurs critiques
- **Toast** - Pour feedbacks utilisateur (rare)

---

## 📊 STATISTIQUES DE L'APPLICATION

### **Fichiers:**
- **71 fichiers Kotlin** au total
- **7 modules fonctionnels** principaux
- **10+ ViewModels**
- **7 Repositories**
- **5 Interfaces API** Retrofit
- **30+ Vues** Compose

### **Endpoints API utilisés:**
- **~25 endpoints REST** différents
- **1 WebSocket namespace** (chat)

---

## 🚀 WORKFLOWS PRINCIPAUX

### **1. Ajout d'un vêtement:**
```
DressingTab → ImageSourceDialog → Camera/Gallery → Upload → /detect → 
AIAnalysisLoadingScreen → DetectionResultBottomSheet → Corrections (optionnel) → 
POST /cloth → DressingTab (refresh)
```

### **2. Mise en vente d'un article:**
```
StoreTab → FloatingAddButton → AddToStoreSheet → Select Cloth → 
Set Price/Size → POST /store → StoreTab (refresh)
```

### **3. Chat avec vendeur:**
```
StoreTab → Discover Item → EditStoreDialog → Contact Owner → 
POST /chat/conversations → ChatDetailView → WebSocket connect → 
Send/Receive messages
```

### **4. Connexion:**
```
IntroView → LoginView → AuthService.signin() → 
TokenManager.saveToken() → TokenManager.saveUserId() → 
TokenManager.saveGender() → MainScreen
```

---

**Document créé le:** 2025-01-23  
**Version Android:** Jetpack Compose  
**Architecture:** MVVM  
**API Backend:** NestJS (http://10.0.2.2:3000 pour émulateur)

