# 🛒 Implémentation Panier avec Paiement Balance/Carte - Android

## 📋 Résumé

Implémentation complète du système de panier avec choix de paiement (balance ou carte Stripe) dans Android, aligné avec l'implémentation iOS existante.

## 🔄 Routes et Endpoints Utilisés

### Backend (Non modifié)

1. **`POST /store/payment-intent`**
   - Crée un Payment Intent Stripe
   - Body: `{ amount: Double, currency: String? }`
   - Retour: `{ clientSecret: String }`

2. **`POST /store/purchase/:id`**
   - Confirme un achat
   - Body: `{ paymentMethod: "balance" | "card", paymentIntentId?: String }`
   - Retour: `StoreItem`

3. **`GET /auth/profile`**
   - Récupère le profil utilisateur avec le balance
   - Retour: `User { balance: Double }`

## 📁 Fichiers Créés/Modifiés

### ✅ Nouveaux Fichiers

1. **`PaymentViewModel.kt`**
   - Gère la logique de paiement (balance et Stripe)
   - Rafraîchit le balance utilisateur
   - Gère les deux méthodes de paiement

### ✅ Fichiers Modifiés

1. **`StoreApi.kt`**
   - `ConfirmPurchaseRequest` : Ajout de `paymentMethod` (requis) et `paymentIntentId` (optionnel)

2. **`StoreRepository.kt`**
   - `confirmPurchase()` : Ajout du paramètre `paymentMethod` et gestion des erreurs de solde insuffisant

3. **`CartView.kt`**
   - Intégration de `PaymentViewModel`
   - Affichage du balance disponible
   - Choix entre balance et carte (radio buttons)
   - Overlay de chargement pendant le paiement
   - Gestion des erreurs et succès

## 🎨 UI/UX Android (Material Design)

### Composants Ajoutés

1. **Affichage du Balance**
   - Ligne "Available Balance" avec couleur conditionnelle (vert si suffisant, rouge sinon)

2. **Choix de Méthode de Paiement**
   - Radio buttons Material Design pour Balance/Card
   - Couleurs thématiques (Teal pour Balance, Primary pour Card)

3. **Bouton Pay Now**
   - Désactivé si balance insuffisant et méthode = balance
   - Message d'erreur "Insufficient balance" affiché en dessous
   - Overlay de chargement pendant le traitement

4. **Dialogs**
   - Dialog de succès après paiement réussi
   - Snackbars pour les erreurs

## 🔧 Logique d'Implémentation

### Flow de Paiement avec Balance

1. Utilisateur sélectionne "Balance"
2. Vérification `canPayWithBalance(totalPrice)`
3. Si OK → Appel `confirmPurchase(paymentMethod: "balance")` pour chaque article
4. Rafraîchissement du balance après achat
5. Vidage du panier
6. Affichage du dialog de succès

### Flow de Paiement avec Carte

1. Utilisateur sélectionne "Card"
2. Appel `createPaymentIntent(amount, currency)`
3. Présentation du Stripe PaymentSheet
4. Après succès → Appel `confirmPurchase(paymentMethod: "card", paymentIntentId)` pour chaque article
5. Rafraîchissement du balance (pour sync)
6. Vidage du panier
7. Affichage du dialog de succès

## 📊 Comparaison iOS vs Android

| Fonctionnalité | iOS | Android | État |
|----------------|-----|---------|------|
| Affichage balance | ✅ | ✅ | Aligné |
| Choix balance/carte | ✅ | ✅ | Aligné |
| Paiement balance | ✅ | ✅ | Aligné |
| Paiement Stripe | ✅ | ✅ | Aligné |
| Vérification solde | ✅ | ✅ | Aligné |
| Overlay chargement | ✅ | ✅ | Aligné |
| Messages erreur | ✅ | ✅ | Aligné |
| Dialog succès | ✅ | ✅ | Aligné |

## 🎯 Points Clés

### ✅ Respect du Backend
- Aucune modification du backend
- Utilisation des endpoints existants
- Format des requêtes conforme

### ✅ Design Android
- Material Design 3
- Radio buttons natifs
- Cards avec elevation
- Snackbars pour feedback
- Dialogs Material

### ✅ Gestion d'Erreurs
- Solde insuffisant : Message clair
- Erreurs réseau : Snackbar avec message
- Erreurs Stripe : Gestion via PaymentSheetResult

### ✅ Performance
- Rafraîchissement du balance uniquement quand nécessaire
- Pas de requêtes inutiles
- Gestion asynchrone avec Coroutines

## 🚀 Utilisation

Le `CartView` est déjà intégré dans `MainScreen.kt`. Aucune modification nécessaire dans la navigation.

```kotlin
CartView(
    token = authToken,
    userId = userId,
    onNavigateBack = { showCart = false }
)
```

Les ViewModels sont créés automatiquement via `viewModel()`.

## 📝 Notes Techniques

1. **Balance en TND** : Le backend stocke en centimes, mais retourne en TND pour l'affichage
2. **PaymentIntentId** : Extrait du `clientSecret` (format: `pi_xxx_secret_yyy`)
3. **Vidage Panier** : Nécessite le `context`, donc appelé depuis la vue
4. **Refresh Balance** : Automatique après chaque achat réussi

## ✅ Tests Recommandés

1. ✅ Paiement avec balance suffisant
2. ✅ Paiement avec balance insuffisant (message d'erreur)
3. ✅ Paiement avec carte (Stripe PaymentSheet)
4. ✅ Annulation du paiement Stripe
5. ✅ Erreur réseau pendant le paiement
6. ✅ Rafraîchissement du balance après achat

---

**Date**: 2025-01-27
**Statut**: ✅ Implémentation complète et alignée avec iOS

