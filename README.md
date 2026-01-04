# Styleto Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2024+-green)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.09-blue)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue)](./LICENSE)

A modern Android application for **Styleto**, a fashion e-commerce platform powered by AI. Built with Kotlin, Jetpack Compose, and Material Design 3, offering a seamless shopping experience with AI-powered clothing detection, outfit recommendations, and a marketplace.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Building the App](#-building-the-app)
- [Project Structure](#-project-structure)
- [Key Features Implementation](#-key-features-implementation)
- [API Integration](#-api-integration)
- [Testing](#-testing)
- [Contributing](#-contributing)

---

## ✨ Features

### 🔐 Authentication
- **Email/Password authentication** with secure JWT token management
- **Google Sign-In** integration
- **Forgot password** flow with OTP verification
- **Automatic token refresh** mechanism
- **Secure credential storage**

### 👕 Dressing (Wardrobe)
- **AI-powered clothing detection** from photos
- **Clothing categorization** (Top, Bottom, Dress, Shoes, Accessory, Jacket)
- **Style and season classification**
- **Color detection** using AI
- **Photo guide** with tips for better detection
- **Clothing detail editing** and management
- **Mirror view** using CameraX

### 🎨 Outfits (Tenues)
- **AI-powered outfit recommendations**
- **Style-based suggestions** (Casual, Formal, Sporty, etc.)
- **Favorites management**
- **Outfit visualization**
- **Personalized recommendations** based on user preferences

### 🛍️ Store & Marketplace
- **Browse store items** (Discover)
- **My store items** management
- **Add items to store** with images, sizes, and prices
- **Shopping cart** with local persistence (Room)
- **Real-time chat** with sellers using WebSocket
- **Stripe payment integration**
- **Order history** tracking

### 💳 Subscriptions & Payments
- **Subscription plans** (Free, Premium, Pro Seller)
- **Usage statistics** and quota tracking
- **Stripe checkout** integration
- **Balance top-up** functionality
- **Subscription management**

### 👤 Profile & Settings
- **User profile** management
- **Avatar upload** via Cloudinary
- **Theme customization** (Light/Dark mode)
- **Settings** and preferences
- **Account management**

### 📸 Camera & Media
- **CameraX integration** for photo capture
- **Image picker** from gallery
- **Mirror view** functionality
- **Image upload** to Cloudinary

---

## 🛠 Tech Stack

### Core
- **Kotlin 2.0.21** - Modern programming language
- **Android SDK** - API 24+ (Android 7.0+)
- **Gradle 8.13** - Build system

### UI Framework
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material Design 3** - Material You design system
- **Compose Navigation** - Type-safe navigation
- **Coil** - Image loading library

### Architecture
- **MVVM (Model-View-ViewModel)** - Architecture pattern
- **ViewModel** - UI-related data holder
- **LiveData/StateFlow** - Reactive data streams
- **Kotlin Coroutines** - Asynchronous programming

### Networking
- **Retrofit 2.9** - Type-safe HTTP client
- **OkHttp 4.12** - HTTP client with interceptors
- **Gson** - JSON serialization/deserialization
- **Socket.IO** - WebSocket client for real-time chat

### Local Storage
- **Room Database** - Local SQLite database
- **DataStore** - Key-value storage for preferences
- **SharedPreferences** - Simple key-value storage

### Dependency Injection
- **Manual DI** - Dependency injection pattern
- **Repository pattern** - Data layer abstraction

### Image Processing
- **Coil Compose** - Image loading in Compose
- **CameraX** - Camera library
- **Cloudinary** - Cloud image management

### Payment
- **Stripe Android SDK** - Payment processing

### Authentication
- **Google Sign-In** - Google authentication
- **JWT** - Token-based authentication

---

## 🏗 Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture with clean separation of concerns:

```
app/src/main/java/tn/esprit/labasniandroid/
├── api/                    # API interfaces and clients
│   ├── RetrofitClient.kt
│   ├── AuthApi.kt
│   ├── ClothesApi.kt
│   └── ...
├── data/                   # Data layer
│   ├── local/              # Local data sources (Room)
│   └── remote/             # Remote data sources (API)
├── models/                 # Data models
│   ├── entities/           # Domain entities
│   └── repositories/       # Repository implementations
├── ui/                     # UI layer
│   ├── screen/             # Screen composables
│   ├── components/         # Reusable components
│   └── theme/              # Theme configuration
└── utils/                  # Utilities and helpers
```

### Architecture Components
- **ViewModels**: Business logic and state management
- **Repositories**: Data access abstraction
- **Use Cases**: Business logic encapsulation
- **Data Sources**: Local (Room) and Remote (API)

---

## 📦 Prerequisites

Before you begin, ensure you have:

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 11** or higher
- **Android SDK** with API 24+ installed
- **Git**
- **Backend API** running (see Backend README)

### Optional
- **Physical Android device** or **Android Emulator**
- **Google Cloud Console** account (for Google Sign-In)

---

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Labasni-Android
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

4. **Configure local.properties**
   - Create or edit `local.properties` file
   - Add your configuration (see [Configuration](#-configuration))

5. **Build the project**
   ```bash
   ./gradlew build
   ```

---

## ⚙️ Configuration

Create or edit `local.properties` in the root directory:

```properties
# Backend API URL
labasni.baseUrl=http://10.0.2.2:3000

# Stripe Configuration
STRIPE_PUBLISHABLE_KEY=pk_test_your-stripe-publishable-key

# Google Sign-In
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

### Configuration Details

#### Backend URL
L'application utilise une logique intelligente pour déterminer quelle URL utiliser :

**Priorité :**
1. **URL configurée dans `local.properties`** (via `labasni.baseUrl`) → **priorité absolue**
   - Si HTTPS (production/déployé) : utilisée pour **tous** les appareils (émulateur et physique)
   - Si HTTP (développement local) : utilisée selon le contexte
2. **Détection automatique** : Si pas d'URL configurée
   - Émulateur → `http://10.0.2.2:3000`
   - Appareil physique → `http://10.0.2.2:3000`

**Exemples de configuration :**

```properties
# Backend déployé (Render, Heroku, etc.) - Fonctionne partout
labasni.baseUrl=https://labasni-backend-mh3j.onrender.com

# Backend local sur Mac - Appareil physique uniquement
labasni.baseUrl=http://192.168.1.100:3000

# Utiliser automatiquement 10.0.2.2 pour l'émulateur (commenter ou laisser vide)
# labasni.baseUrl=
```

**Pour trouver l'IP locale de votre Mac (pour backend local) :**
```bash
# Terminal
ifconfig | grep "inet " | grep -v 127.0.0.1

# Ou via l'interface graphique :
# Préférences Système > Réseau > Wi-Fi > Détails > TCP/IP > Adresse IPv4
```

> **Note** : Si vous configurez une URL HTTPS (backend déployé), elle sera utilisée pour **tous** les appareils, même l'émulateur. C'est la configuration recommandée pour la production !

#### Stripe
- Get your publishable key from [Stripe Dashboard](https://dashboard.stripe.com/apikeys)
- Use test keys for development

#### Google Sign-In
1. Create a project in [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Google Sign-In API
3. Create OAuth 2.0 credentials
4. Add your package name and SHA-1 fingerprint

---

## 🔨 Building the App

### Debug Build
```bash
./gradlew assembleDebug
```
APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
```bash
./gradlew assembleRelease
```
APK location: `app/build/outputs/apk/release/app-release.apk`

### Install on Device
```bash
./gradlew installDebug
```

### Run Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## 📁 Project Structure

```
app/src/main/
├── java/tn/esprit/labasniandroid/
│   ├── api/                        # API layer
│   │   ├── RetrofitClient.kt      # Retrofit instance
│   │   ├── TokenRefreshInterceptor.kt
│   │   ├── AuthApi.kt
│   │   ├── ClothesApi.kt
│   │   ├── OutfitsApi.kt
│   │   ├── StoreApi.kt
│   │   ├── CartApi.kt
│   │   ├── ChatApi.kt
│   │   ├── OrdersApi.kt
│   │   └── SubscriptionApi.kt
│   │
│   ├── data/                       # Data layer
│   │   ├── local/                  # Local storage
│   │   │   ├── CartDatabase.kt
│   │   │   ├── dao/CartDao.kt
│   │   │   └── entities/CartItem.kt
│   │   └── remote/                 # Remote data
│   │       └── RemoteDataSource.kt
│   │
│   ├── models/                     # Domain models
│   │   ├── entities/               # Data entities
│   │   │   ├── User.kt
│   │   │   ├── Cloth.kt
│   │   │   ├── Outfit.kt
│   │   │   ├── StoreItem.kt
│   │   │   └── ...
│   │   ├── repositories/           # Repository implementations
│   │   │   ├── AuthRepository.kt
│   │   │   ├── ClothesRepository.kt
│   │   │   └── ...
│   │   └── Responses.kt
│   │
│   ├── ui/                         # UI layer
│   │   ├── screen/                 # Screens
│   │   │   ├── auth/               # Authentication screens
│   │   │   ├── dressing/           # Wardrobe screens
│   │   │   ├── tenues/             # Outfits screens
│   │   │   ├── store/              # Store screens
│   │   │   ├── profile/            # Profile screens
│   │   │   └── settings/           # Settings screens
│   │   ├── components/             # Reusable components
│   │   │   ├── LabasniComponents.kt
│   │   │   ├── OtpDialogs.kt
│   │   │   └── ...
│   │   └── theme/                  # Theme configuration
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   │
│   └── utils/                      # Utilities
│       ├── APIConstants.kt
│       ├── TokenManager.kt
│       ├── CartManager.kt
│       └── ...
│
├── res/                            # Resources
│   ├── drawable/                   # Drawable resources
│   ├── values/                     # Values (colors, strings, etc.)
│   └── ...
│
└── AndroidManifest.xml             # App manifest
```

---

## 🔑 Key Features Implementation

### Authentication Flow
1. User enters credentials or uses Google Sign-In
2. API call to backend for authentication
3. JWT tokens stored securely using `TokenManager`
4. `TokenRefreshInterceptor` handles automatic token refresh
5. User redirected to main screen

### Clothing Detection
1. User captures/selects photo
2. Photo guide shown with tips
3. Image uploaded to backend `/clothes/detect` endpoint
4. AI processes image and returns detection results
5. User can edit category, style, season, and color
6. Clothing item saved to wardrobe

### Outfit Recommendations
1. User navigates to Outfits screen
2. API call to `/outfits/recommend` with preferences
3. AI generates outfit suggestions
4. User can favorite outfits
5. Outfits displayed with clothing items

### Store & Shopping
1. Browse store items in Discover tab
2. View item details
3. Add to cart (stored locally in Room)
4. Real-time chat with seller via WebSocket
5. Proceed to checkout with Stripe
6. Order created and tracked

### Real-Time Chat
1. WebSocket connection established via `VTOWebSocketManager`
2. Messages sent/received in real-time
3. Typing indicators shown
4. Message history persisted
5. Conversations list updated

---

## 🔌 API Integration

### Base Configuration
- **Base URL**: Configured in `local.properties`
- **Retrofit Client**: Singleton instance in `RetrofitClient.kt`
- **Interceptors**: 
  - `TokenRefreshInterceptor` for automatic token refresh
  - Logging interceptor for debugging

### API Endpoints Used

#### Authentication
- `POST /auth/signin` - Login
- `POST /auth/signup` - Registration
- `POST /auth/google` - Google Sign-In
- `POST /auth/forgot-password` - Password reset

#### Clothes
- `GET /clothes` - Get user's clothes
- `POST /clothes` - Add clothing item
- `POST /clothes/detect` - AI detection
- `DELETE /clothes/:id` - Delete item

#### Outfits
- `GET /outfits` - Get outfits
- `POST /outfits/recommend` - Get recommendations

#### Store
- `GET /store` - Get store items
- `POST /store` - Create store item
- `PUT /store/:id` - Update item

#### Cart & Orders
- `GET /cart` - Get cart
- `POST /cart` - Add to cart
- `POST /orders` - Create order

#### Chat
- `GET /chat/conversations` - Get conversations
- `GET /chat/messages/:id` - Get messages
- WebSocket: `/chat` namespace

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Test Structure
- Unit tests in `src/test/java/`
- Instrumented tests in `src/androidTest/java/`

---

## 🎨 UI/UX Features

### Material Design 3
- Modern Material You design
- Dynamic color theming
- Smooth animations and transitions

### Dark Mode Support
- Automatic dark mode based on system settings
- Custom theme switching

### Responsive Design
- Adaptive layouts for different screen sizes
- Tablet and phone support

### Accessibility
- Content descriptions
- Touch target sizes
- Screen reader support

---

## 🔒 Security

### Token Management
- Secure token storage
- Automatic token refresh
- Token expiration handling

### Network Security
- HTTPS for all API calls
- Certificate pinning (optional)
- Secure credential storage

### Data Protection
- Encrypted local storage
- Secure password handling
- OAuth 2.0 for third-party auth

---

## 🚢 Deployment

### Release Build Steps

1. **Update version**
   - Update `versionCode` and `versionName` in `build.gradle.kts`

2. **Generate signed APK**
   ```bash
   ./gradlew assembleRelease
   ```

3. **Sign the APK** (if not using automatic signing)
   - Use Android Studio's Build > Generate Signed Bundle/APK

4. **Upload to Play Store**
   - Create release in Google Play Console
   - Upload APK/AAB
   - Complete store listing

### ProGuard/R8
- ProGuard rules in `proguard-rules.pro`
- Enable minification for release builds

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use ktlint for code formatting
- Write unit tests for new features
- Update documentation

---

## 📝 License

This project is licensed under the MIT License.

---

## 📞 Support

For support, email support@styleto.com or open an issue in the repository.

---

## 🙏 Acknowledgments

- Jetpack Compose team
- Material Design team
- All open-source library contributors
- The Android developer community

---

**Built with ❤️ for Styleto**

