# 🔄 ADAPTATION DRESSING ANDROID → iOS

## 📋 RÉSUMÉ DES CHANGEMENTS

Ce document liste tous les changements nécessaires pour que la page Dressing Android soit **identique** à iOS.

---

## ✅ CHANGEMENTS COMPLÉTÉS

1. ✅ **PhotoGuidePopupView.kt** - Popup avec carousel de tips créé
2. ✅ **Cloth.kt** - Modèle mis à jour avec `season`, `style`, `color` (champs optionnels)
3. ✅ **ClothResponse.toEntity()** - Mapping mis à jour pour inclure season, style, color

---

## 🔄 CHANGEMENTS EN COURS

### **1. Workflow Photo Guide**
- ❌ **Avant:** Bouton '+' → ImageSourceDialog direct
- ✅ **Après:** Bouton '+' → PhotoGuidePopupView → Camera/Gallery

### **2. Pull-to-Refresh**
- ❌ **Manquant:** Pas de pull-to-refresh sur le ScrollView
- ✅ **À ajouter:** `pullRefreshIndicator()` avec `PullRefreshState`

### **3. Bordure Colorée sur Cartes**
- ❌ **Avant:** Juste un background coloré selon catégorie
- ✅ **Après:** Bordure fine (2.5dp) colorée selon catégorie (comme iOS)

### **4. Saison dans les Cartes**
- ❌ **Manquant:** Pas d'affichage de la saison sous la catégorie
- ✅ **À ajouter:** Afficher `cloth.season` sous `cloth.type` (comme iOS)

### **5. ClothingDetailSheet**
- ❌ **Manquant:** Pas de sheet détaillée lors du clic sur une carte
- ✅ **À créer:** Sheet complète avec:
  - Image du vêtement
  - Badge "AI Analyzed"
  - Détails (Type, Color, Style, Season)
  - Owner info (si disponible)
  - Bouton Delete en bas

### **6. DetectionResultView FullScreen**
- ❌ **Avant:** DetectionResultBottomSheet (ModalBottomSheet)
- ✅ **Après:** DetectionResultView en fullScreenCover (comme iOS)

### **7. Animation de Suppression**
- ❌ **Manquant:** Pas d'animation lors de la suppression
- ✅ **À ajouter:** Animation opacity + scale (comme iOS)

### **8. Catégories Standardisées**
- ❌ **Avant:** `["All", "Tshirt", "Pants", "Dress", "Shoes", "Accessory"]`
- ✅ **Après:** `["All", "Top", "Bottom", "Dress", "Shoes", "Accessory", "Jacket"]` (comme iOS)

### **9. Filtrage Amélioré**
- ❌ **Avant:** Filtre seulement par `type` et `name`
- ✅ **Après:** Filtre par `type`, `color`, `style`, `season` (comme iOS DressingViewModel)

---

## 📁 FICHIERS À MODIFIER/CRÉER

### **Nouveaux Fichiers**
1. ✅ `PhotoGuidePopupView.kt` - **CRÉÉ**
2. ❌ `ClothingDetailSheet.kt` - **À CRÉER**
3. ❌ `DetectionResultView.kt` (remplace BottomSheet) - **À CRÉER/ADAPTER**

### **Fichiers à Modifier**
1. ❌ `DressingView.kt` - **À ADAPTER COMPLÈTEMENT**
   - Workflow PhotoGuide
   - Pull-to-refresh
   - Bordure colorée sur cartes
   - Saison dans cartes
   - Clic sur carte → ClothingDetailSheet
   - Animation suppression
   - Catégories standardisées
   - Filtrage amélioré

2. ✅ `Cloth.kt` - **MODIFIÉ** (season, style, color ajoutés)
3. ✅ `DressingRepository.kt` - **MODIFIÉ** (mapping ClothResponse)

### **Dépendances à Vérifier**
- `androidx.compose.foundation:foundation-pager` - Pour HorizontalPager (devrait être inclus dans ComposeBOM)

---

## 🎨 DÉTAILS UI (IDENTIQUES À iOS)

### **PhotoGuidePopupView**
- Dialog plein écran avec overlay noir 40% opacity
- Card centrée avec radius 28dp
- Header avec icône caméra + titre "Photo Tips" + bouton close
- HorizontalPager avec 4 tips (carousel)
- Indicateurs de page en bas du carousel
- Bouton "Got it!" avec gradient primary → teal

### **ClothingDetailSheet**
- ModalBottomSheet (pas Dialog)
- Image du vêtement (hauteur 300dp) avec fond damier
- Badge "AI Analyzed" en haut à droite
- Sections:
  - Details (Type, Color avec cercle, Style, Season avec icône)
  - Owner (si disponible) avec avatar et nom/email
  - Delete Button (rouge avec gradient) en bas

### **ClothingCard**
- Bordure colorée fine (2.5dp) selon catégorie:
  - Top: #A7E0E0 (Teal clair)
  - Bottom/Pants: #4D5F8F (Bleu marine)
  - Dress: #DB6A8F (Rose vif)
  - Shoes: #4A4A4A (Gris foncé)
  - Accessory/Jacket: #E8AABE (Rose doux)
- Image: 140dp de hauteur
- Infos: Catégorie (18sp, bold) + Saison (13sp, secondary) si disponible
- Bouton trash: 32dp, circle, opacity 0.15 background primary
- Animation suppression: opacity → 0, scale → 0.95

### **Pull-to-Refresh**
- Indicateur de refresh natif Android
- Action: `viewModel.loadClothes(token)` lors du refresh

---

## 🔄 WORKFLOW COMPLET (IDENTIQUE À iOS)

1. **Ajouter un vêtement:**
   - Utilisateur clique sur bouton '+' (flottant, bas droite)
   - PhotoGuidePopupView s'affiche
   - Utilisateur swipe dans le carousel pour voir les tips
   - Utilisateur clique "Got it!"
   - ImageSourceDialog s'affiche (Camera/Gallery)
   - Utilisateur prend/sélectionne une photo
   - AIAnalysisLoadingScreen s'affiche (fullScreen)
   - Détection terminée → DetectionResultView s'affiche (fullScreenCover)
   - Utilisateur édite les détails (Category, Color, Style, Season)
   - Utilisateur clique "Add to Wardrobe"
   - Vêtement ajouté, liste refreshée automatiquement

2. **Voir les détails:**
   - Utilisateur clique sur une carte ClothingCard
   - ClothingDetailSheet s'affiche (ModalBottomSheet)
   - Utilisateur voit tous les détails + owner info
   - Utilisateur peut supprimer via bouton Delete en bas

3. **Supprimer un vêtement:**
   - Option 1: Via ClothingDetailSheet → Bouton Delete
   - Option 2: Via bouton trash sur la carte → AlertDialog
   - Animation: opacity → 0, scale → 0.95
   - Vêtement retiré de la liste après suppression réussie

---

## ✅ CHECKLIST FINALE

- [x] PhotoGuidePopupView créé
- [x] Cloth.kt mis à jour (season, style, color)
- [x] ClothResponse.toEntity() mis à jour
- [ ] Workflow PhotoGuide → Camera implémenté
- [ ] Pull-to-refresh ajouté
- [ ] Bordure colorée sur cartes
- [ ] Saison affichée dans cartes
- [ ] ClothingDetailSheet créé
- [ ] DetectionResultView en fullScreenCover
- [ ] Animation suppression implémentée
- [ ] Catégories standardisées
- [ ] Filtrage amélioré
- [ ] Tous les tests passent
- [ ] UI identique à iOS

---

**Date de création:** 2025-01-XX  
**Dernière mise à jour:** 2025-01-XX

