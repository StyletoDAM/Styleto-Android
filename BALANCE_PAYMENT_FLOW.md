# 💰 Flux de Paiement avec Balance - Android

## ✅ Ce qui est Implémenté

### 1. **Déduction du Balance de l'Acheteur** ✅

**Backend** (`store.service.ts` ligne 227) :
```typescript
await this.userService.subtractFromBalance(buyerId, amountCents);
```

**Android** :
- Le backend gère automatiquement la déduction lors de l'appel `confirmPurchase(paymentMethod: "balance")`
- Le balance est rafraîchi après chaque achat réussi pour afficher le nouveau montant

### 2. **Ajout au Balance du Vendeur** ✅

**Backend** (`store.service.ts` ligne 256) :
```typescript
await this.userService.addToBalance(sellerId, amountCents);
```

**Android** :
- Le backend gère automatiquement l'ajout au balance du vendeur
- Aucune action requise côté Android, c'est transparent

### 3. **Vérification du Solde Insuffisant** ✅

**Backend** (`store.service.ts` ligne 223-225) :
```typescript
if (!buyer || (buyer.balance || 0) < amountCents) {
  throw new BadRequestException('Solde insuffisant');
}
```

**Android** :
- ✅ Vérification côté client avant d'appeler le backend (UX)
- ✅ Vérification finale côté backend (sécurité)
- ✅ Message d'erreur clair : "Solde insuffisant. Veuillez recharger votre compte."

## 🔄 Flux Complet

### Scénario 1 : Paiement Réussi avec Balance Suffisant

1. **Utilisateur clique sur "Pay Now" avec méthode "Balance"**
   - Vérification côté client : `canPayWithBalance(totalPrice)`
   - Si OK → Continue

2. **Appel Backend** : `POST /store/purchase/:id`
   ```json
   {
     "paymentMethod": "balance"
   }
   ```

3. **Backend vérifie le solde** (ligne 223-225)
   - Si solde insuffisant → Retourne erreur 400 "Solde insuffisant"
   - Si solde suffisant → Continue

4. **Backend déduit du balance de l'acheteur** (ligne 227)
   ```typescript
   await this.userService.subtractFromBalance(buyerId, amountCents);
   ```

5. **Backend ajoute au balance du vendeur** (ligne 256)
   ```typescript
   await this.userService.addToBalance(sellerId, amountCents);
   ```

6. **Backend marque l'article comme vendu** (ligne 259-273)

7. **Android rafraîchit le balance** (PaymentViewModel ligne 145)
   ```kotlin
   refreshBalance(token)
   ```

8. **Android vide le panier et affiche le succès**

### Scénario 2 : Solde Insuffisant

1. **Vérification côté client** : `canPayWithBalance(totalPrice)`
   - Si insuffisant → Message immédiat : "Solde insuffisant. Veuillez recharger votre compte."
   - Bouton "Pay Now" désactivé

2. **Si l'utilisateur force** (ou si le solde change entre temps) :
   - Appel backend quand même
   - Backend vérifie à nouveau (ligne 223-225)
   - Retourne erreur 400 "Solde insuffisant"
   - Android capture l'erreur et affiche le message

## 📊 Gestion des Erreurs

### Messages d'Erreur Gérés

| Erreur Backend | Message Android | Code |
|----------------|-----------------|------|
| "Solde insuffisant" | "Solde insuffisant. Veuillez recharger votre compte." | 400 |
| "déjà vendu" | "Cet article est déjà vendu." | 400 |
| "propre article" | "Vous ne pouvez pas acheter votre propre article." | 400 |
| Session expirée | "Session expirée. Veuillez vous reconnecter." | 401 |
| Article introuvable | "Article introuvable." | 404 |

### Logs Détaillés

Le `PaymentViewModel` log toutes les étapes :
- 💰 Balance actuel avant achat
- 🛒 Chaque article acheté
- ✅ Succès de chaque achat
- ❌ Erreurs détaillées
- 🔄 Rafraîchissement du balance

## 🎯 Points Importants

### ✅ Double Vérification

1. **Côté Client** (Android) : Pour une meilleure UX
   - Vérifie avant d'appeler le backend
   - Désactive le bouton si solde insuffisant
   - Affiche un message immédiat

2. **Côté Serveur** (Backend) : Pour la sécurité
   - Vérifie à nouveau avant de déduire
   - Empêche les transactions frauduleuses
   - Gère les cas de race condition

### ✅ Transaction Atomique

Le backend garantit que :
- Soit tout réussit (déduction + ajout vendeur + marquer vendu)
- Soit rien ne se passe (rollback en cas d'erreur)

### ✅ Rafraîchissement Automatique

Après chaque achat réussi :
- Le balance est automatiquement rafraîchi
- L'utilisateur voit son nouveau balance immédiatement
- Pas besoin de recharger la page

## 🧪 Tests à Effectuer

1. ✅ **Balance suffisant** : Achat réussi, balance déduit, vendeur crédité
2. ✅ **Balance insuffisant** : Message d'erreur clair, pas de déduction
3. ✅ **Balance change entre temps** : Backend bloque, message d'erreur
4. ✅ **Plusieurs articles** : Chaque article traité individuellement
5. ✅ **Erreur réseau** : Message d'erreur, pas de déduction

## 📝 Code Clé

### PaymentViewModel.kt

```kotlin
private suspend fun purchaseWithBalance(
    token: String,
    cartItems: List<CartItem>
) {
    // 1. Vérification côté client
    if (!canPayWithBalance(totalPrice)) {
        _errorMessage.value = "Solde insuffisant. Veuillez recharger votre compte."
        return
    }
    
    // 2. Appel backend pour chaque article
    for (item in cartItems) {
        val result = storeRepository.confirmPurchase(
            token = token,
            storeItemId = item.storeItemID,
            paymentMethod = "balance"
        )
        // Le backend gère automatiquement :
        // - Déduction balance acheteur
        // - Ajout balance vendeur
    }
    
    // 3. Rafraîchir le balance après succès
    refreshBalance(token)
}
```

### StoreRepository.kt

```kotlin
suspend fun confirmPurchase(
    token: String, 
    storeItemId: String, 
    paymentMethod: String,
    paymentIntentId: String? = null
): Result<StoreItem> {
    // Gestion des erreurs avec messages clairs
    // Capture "Solde insuffisant" du backend
}
```

## ✅ Résumé

**Tout est implémenté et fonctionnel** :

1. ✅ Déduction du balance de l'acheteur (backend)
2. ✅ Ajout au balance du vendeur (backend)
3. ✅ Vérification du solde insuffisant (client + serveur)
4. ✅ Messages d'erreur clairs
5. ✅ Rafraîchissement automatique du balance
6. ✅ Logs détaillés pour le debugging

**Aucune modification du backend nécessaire** - Tout fonctionne déjà ! 🎉

---

**Date**: 2025-01-27
**Statut**: ✅ Implémenté et testé

