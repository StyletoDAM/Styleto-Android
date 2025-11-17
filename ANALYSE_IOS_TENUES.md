# Analyse détaillée de la page Tenues iOS

## 📋 Structure générale

### Layout principal
- **Container**: `NavigationStack` avec `ScrollView`
- **Padding horizontal**: `16.dp`
- **Padding top**: `12.dp`
- **Padding bottom**: `32.dp`
- **Spacing vertical**: `24.dp` entre les sections
- **Background**: `Color.themeBackground` (ignore safe area)

---

## 🎨 Header

### Titre "My Outfits"
- **Texte**: "My Outfits"
- **Font**: `.system(size: 36, weight: .bold)`
- **Couleur**: `.themePrimary`
- **Alignment**: `.leading` avec `maxWidth: .infinity`
- **Position**: En haut de la page

### Bouton Favoris (Toolbar)
- **Position**: `navigationBarTrailing`
- **Icône**: `heart.fill`
- **Font**: `.system(size: 20, weight: .semibold)`
- **Couleur**: `.themePrimary`
- **Action**: Navigation vers `FavoritesView`

---

## 💡 Suggestion Card (Carte de suggestion du jour)

### Style général
- **Background**: `LinearGradient` de `.themeSecondary` à `.themePrimary`
  - `startPoint: .topLeading`
  - `endPoint: .bottomTrailing`
- **Corner radius**: `28` (style: `.continuous`)
- **Padding**: `20.dp`
- **Shadow**: `color: .black.opacity(0.12), radius: 16, x: 0, y: 10`
- **Spacing interne**: `12.dp`

### Contenu
1. **Titre "Today's Suggestion"**
   - Font: `.system(size: 22, weight: .bold)`
   - Couleur: `.white`

2. **Description**
   - Texte: "The weather is nice today! Why not try a light and colorful outfit?"
   - Couleur: `.white.opacity(0.95)`

3. **Bouton "See suggestion"**
   - Font: `.system(size: 16, weight: .semibold)`
   - Padding horizontal: `20.dp`
   - Padding vertical: `12.dp`
   - Background: `Color.white`
   - Foreground: `.themePrimary`
   - Shape: `Capsule()`
   - Action: Ouvre `StyleSelectionPopup` (`showStylePopup = true`)

### StyleSelectionPopup
- **Titre**: "Which style are you looking for today?"
  - Font: `.title2.bold()`
  - Alignment: `.center`
  - Padding top: `20.dp`

- **Styles disponibles**: `["Casual", "Formal", "Sporty", "Elegant", "Party"]`

- **Boutons de style**:
  - Font: `.headline`
  - Foreground: `.white`
  - Frame: `maxWidth: .infinity`
  - Padding: `16.dp`
  - Background: `LinearGradient` de `.themePrimary` à `.themeSecondary`
  - Shape: `Capsule()`
  - Action: Sélectionne le style et ferme le popup, puis génère une suggestion

- **Bouton Cancel**:
  - Font: `.subheadline.bold()`
  - Foreground: `.red`
  - Padding bottom: `20.dp`

---

## 🎯 SuggestionCard (Carte affichée après génération)

### Style général
- **Background**: `Color.themeCard`
- **Corner radius**: `24.dp`
- **Padding**: `16.dp`
- **Shadow**: `color: .black.opacity(0.1), radius: 8, x: 0, y: 6`
- **Padding horizontal**: `16.dp` (externe)
- **Spacing interne**: `16.dp`

### Contenu
1. **Titre "Your Style Suggestion"**
   - Font: `.title2.bold()`
   - Couleur: `.themePrimary`

2. **Preview des vêtements**:
   - `HStack` avec spacing: `12.dp`
   - Images: `80x80.dp`
   - Corner radius: `16.dp`
   - Placeholder: `RoundedRectangle` avec `Color.themeSoftPink.opacity(0.2)`

3. **Boutons d'action**:
   - `HStack` avec spacing: `12.dp`
   
   - **Bouton "Reject"**:
     - Font: `.headline`
     - Foreground: `.white`
     - Frame: `maxWidth: .infinity`
     - Padding: `16.dp`
     - Background: `Color.red.opacity(0.85)`
     - Shape: `Capsule()`
   
   - **Bouton "Accept"**:
     - Font: `.headline`
     - Foreground: `.white`
     - Frame: `maxWidth: .infinity`
     - Padding: `16.dp`
     - Background: `Color.green.opacity(0.85)`
     - Shape: `Capsule()`

### Animation
- **Transition**: `.move(edge: .top).combined(with: .opacity)`
- **Animation**: `.easeInOut` avec `value: viewModel.suggestion != nil`

---

## 📑 Section Header

### "Recent Outfits"
- **Texte**: "Recent Outfits"
- **Font**: `.system(size: 22, weight: .semibold)`
- **Couleur**: `.themeTeal`

---

## 📋 TenueCard (Carte de tenue)

### Style général
- **Background**: `Color.themeCard` (ou `.themeCard.opacity(0.95)` si suggestion)
- **Corner radius**: `28.dp`
- **Padding**: `18.dp`
- **Shadow**: `color: .black.opacity(0.08), radius: 12, x: 0, y: 8`
- **Spacing interne**: `12.dp`
- **Animation**: `.easeInOut(duration: 0.3)` pour `isSuggestion`

### Structure

#### 1. Header (HStack)
- **Alignment**: `.top`
- **Spacing**: Automatique

   - **Titre et sous-titre** (VStack, alignment: `.leading`, spacing: `4.dp`):
     - **Titre**: `outfit.title` (eventType ou "Outfit")
       - Font: `.system(size: 22, weight: .semibold)`
       - Couleur: `.themeTeal`
     - **Sous-titre**: "X article(s)"
       - Font: `.subheadline`
       - Couleur: `.themeSecondaryText`

   - **Bouton Favoris**:
     - Icône: `heart.fill` si favori, `heart` sinon
     - Font: `.system(size: 20, weight: .semibold)`
     - Couleur: `.themePrimary` si favori, `.themeSecondary` sinon
     - Padding: `8.dp`
     - Background: `Color.themeCard.opacity(0.8)`
     - Shape: `Circle()`
     - Shadow: `radius: 2`
     - Action: Toggle favori via `FavoritesService.shared.toggleFavorite(outfitId: outfit.id)`

#### 2. Preview des vêtements (HStack)
- **Spacing**: `14.dp`
- **Images**: `56x56.dp`
- **Corner radius**: `16.dp`
- **Placeholder**: `RoundedRectangle` avec `Color.themeSoftPink.opacity(0.2)`
- **Maximum 3 images** affichées
- **Placeholders supplémentaires** avec icône `plus` en gris si moins de 3 images

#### 3. Date (HStack)
- **Spacing**: `8.dp`
- **Icône**: `calendar`
  - Couleur: `.themeTeal.opacity(0.7)`
- **Texte**: `outfit.dateLabel` (format relatif, ex: "2 days ago")
  - Font: `.subheadline`
  - Couleur: `.themeTeal.opacity(0.7)`

#### 4. Boutons d'action (uniquement si `isSuggestion`)
- **HStack** avec spacing: `12.dp`
- **Padding top**: `8.dp`

   - **Bouton "Reject"**:
     - Font: `.system(size: 15, weight: .semibold)`
     - Foreground: `.white`
     - Frame: `maxWidth: .infinity`
     - Padding vertical: `10.dp`
     - Background: `Color.red.opacity(0.9)`
     - Shape: `Capsule()`
   
   - **Bouton "Accept"**:
     - Font: `.system(size: 15, weight: .semibold)`
     - Foreground: `.white`
     - Frame: `maxWidth: .infinity`
     - Padding vertical: `10.dp`
     - Background: `Color.green.opacity(0.9)`
     - Shape: `Capsule()`

---

## 🎨 Empty State

### Quand aucune tenue
- **VStack** avec spacing: `16.dp`
- **Frame**: `maxWidth: .infinity`
- **Padding top**: `40.dp`

- **Icône**: `tshirt`
  - Font: `.system(size: 50)`
  - Couleur: `.gray`

- **Texte**: "No outfits at the moment"
  - Font: `.title3`
  - Couleur: `.themeSecondaryText`

- **Bouton**: "Generate an outfit"
  - Style: `.borderedProminent`
  - Tint: `.themeTeal`
  - Action: `viewModel.generateSuggestion()`

---

## 🔄 États de chargement

### Loading
- **ProgressView** centré
- **Frame**: `maxWidth: .infinity`
- **Padding**: `16.dp`

### Error
- **Texte**: `viewModel.errorMessage`
- **Couleur**: `.red`
- **Frame**: `maxWidth: .infinity`

---

## 🎨 Couleurs utilisées

### Thème dynamique (selon genre et mode)
- **`.themePrimary`**: 
  - Light + Female: `#CA3C66` (Rose)
  - Light + Male: `#4AA3A2` (Teal)
  - Dark + Female: `#E85C8A` (Rose clair)
  - Dark + Male: `#6BC4C3` (Teal clair)

- **`.themeSecondary`**:
  - Light + Female: `#DB6A8F` (Rose secondaire)
  - Light + Male: `#6BC4C3` (Aqua)
  - Dark + Female: `#F07BA3` (Rose secondaire clair)
  - Dark + Male: `#B8E8E8` (Aqua clair)

- **`.themeTeal`**:
  - Light + Female: `#4AA3A2` (Teal)
  - Light + Male: `#CA3C66` (Rose - inversé)
  - Dark + Female: `#6BC4C3` (Teal clair)
  - Dark + Male: `#E85C8A` (Rose clair - inversé)

- **`.themeCard`**: 
  - Light: `Color.white`
  - Dark: `#2A2A3E`

- **`.themeBackground`**: 
  - Light: `Color(.systemGroupedBackground)`
  - Dark: `#1A1A2E`

- **`.themeSecondaryText`**: 
  - Light: `Color.secondary`
  - Dark: `#B0B0B0`

- **`.themeSoftPink`**:
  - Light + Female: `#E8AABE`
  - Light + Male: `#A7E0E0`
  - Dark + Female: `#F5B5C8`
  - Dark + Male: `#B8E8E8`

### Couleurs fixes
- **Rouge (Reject)**: `Color.red.opacity(0.85)` ou `0.9`
- **Vert (Accept)**: `Color.green.opacity(0.85)` ou `0.9`
- **Gris (Empty state)**: `.gray`

---

## 📏 Tailles et espacements

### Fonts
- **Titre principal**: `36` (bold)
- **Titre section**: `22` (semibold)
- **Titre carte**: `22` (semibold)
- **Body**: `16` (semibold pour boutons)
- **Subheadline**: `15` (pour dates, sous-titres)
- **Headline**: `17` (pour boutons d'action)

### Espacements
- **Spacing principal**: `24.dp` entre sections
- **Spacing cartes**: `18.dp`
- **Spacing interne carte**: `12.dp`
- **Spacing header carte**: `4.dp` (titre/sous-titre)
- **Spacing preview**: `14.dp` (images)
- **Spacing date**: `8.dp` (icône/texte)

### Tailles d'images
- **Preview dans TenueCard**: `56x56.dp`
- **Preview dans SuggestionCard**: `80x80.dp`
- **Corner radius images**: `16.dp`

### Padding
- **Horizontal page**: `16.dp`
- **Top page**: `12.dp`
- **Bottom page**: `32.dp`
- **Carte suggestion**: `20.dp`
- **Carte tenue**: `18.dp`
- **SuggestionCard**: `16.dp`

### Corner radius
- **Suggestion card principale**: `28.dp` (continuous)
- **TenueCard**: `28.dp`
- **SuggestionCard**: `24.dp`
- **Images**: `16.dp`
- **Boutons**: `Capsule()` (plein arrondi)

### Shadows
- **Suggestion card principale**: `radius: 16, x: 0, y: 10, opacity: 0.12`
- **TenueCard**: `radius: 12, x: 0, y: 8, opacity: 0.08`
- **SuggestionCard**: `radius: 8, x: 0, y: 6, opacity: 0.1`
- **Bouton favoris**: `radius: 2`

---

## 🔄 Fonctionnalités

### Pull to refresh
- **Modifier**: `.refreshable { viewModel.loadOutfits() }`

### Navigation
- **Back button**: Caché (`navigationBarBackButtonHidden(true)`)
- **Favoris**: NavigationLink vers `FavoritesView`

### Animations
- **SuggestionCard**: Transition `.move(edge: .top).combined(with: .opacity)`
- **Animation globale**: `.easeInOut` avec `value: viewModel.suggestion != nil`
- **TenueCard suggestion**: `.easeInOut(duration: 0.3)` pour `isSuggestion`

---

## 📱 Messages et textes

### Titres
- "My Outfits" (header)
- "Today's Suggestion" (carte suggestion)
- "Your Style Suggestion" (SuggestionCard)
- "Recent Outfits" (section)
- "My Favorites" (page favoris)

### Messages
- "The weather is nice today! Why not try a light and colorful outfit?" (description suggestion)
- "No outfits at the moment" (empty state)
- "Generate an outfit" (bouton empty state)
- "No favorite outfits" (empty state favoris)
- "Tap the heart in \"My Outfits\" to add them here" (message favoris)

### Boutons
- "See suggestion" (carte suggestion principale)
- "Reject" (SuggestionCard et TenueCard si suggestion)
- "Accept" (SuggestionCard et TenueCard si suggestion)
- "Generate an outfit" (empty state)
- "Cancel" (StyleSelectionPopup)
- Styles: "Casual", "Formal", "Sporty", "Elegant", "Party"

---

## 🎯 Comportements

### Génération de suggestion
1. Clic sur "See suggestion" → Ouvre `StyleSelectionPopup`
2. Sélection d'un style → Ferme le popup
3. `onDisappear` → Appelle `viewModel.generateSuggestion()`
4. Affichage de `SuggestionCard` avec animation

### Acceptation de suggestion
1. Clic sur "Accept" → `viewModel.acceptSuggestion()`
2. Création de l'outfit via API
3. Insertion en première position dans la liste
4. Suppression de la suggestion

### Rejet de suggestion
1. Clic sur "Reject" → `viewModel.rejectSuggestion()`
2. Suppression de la suggestion (si elle existe en backend)
3. `suggestion = nil`

### Toggle favori
1. Clic sur l'icône cœur → `FavoritesService.shared.toggleFavorite(outfitId: outfit.id)`
2. Mise à jour locale via CoreData
3. L'icône change immédiatement (`isFavorite.toggle()`)

---

## 📝 Notes importantes

1. **Pas de bouton d'ajout manuel** : Les tenues sont créées uniquement via les suggestions
2. **Pas de suppression directe** : Pas de bouton delete visible dans TenueCard (sauf pour les suggestions)
3. **Favoris locaux** : Gérés via CoreData, pas via API
4. **Preview limité** : Maximum 3 images par tenue
5. **Date relative** : Utilise `RelativeDateTimeFormatter` pour afficher "2 days ago", etc.
6. **Animation conditionnelle** : Les boutons Accept/Reject n'apparaissent que si `isSuggestion = true`

