#!/bin/bash
# Script de test de l'API

echo "=========================================="
echo "   TEST DE L'API VIENS JOUER"
echo "=========================================="
echo ""

# URL de base
BASE_URL="http://localhost:8080/jakartaee-starter/api"

echo "1. Création d'un utilisateur..."
curl -s -X POST $BASE_URL/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Martin","email":"alice@example.com"}' | json_pp

echo ""
echo "2. Récupération de tous les utilisateurs..."
curl -s -X GET $BASE_URL/users | json_pp

echo ""
echo "3. Création d'une salle..."
curl -s -X POST $BASE_URL/venues \
  -H "Content-Type: application/json" \
  -d '{"name":"Zénith Paris","address":"211 Avenue Jean Jaurès","postalCode":"75019","city":"Paris"}' | json_pp

echo ""
echo "4. Création d'un événement..."
curl -s -X POST $BASE_URL/events \
  -H "Content-Type: application/json" \
  -d '{"title":"Concert Live","description":"Un super concert en direct!"}' | json_pp

echo ""
echo "5. Création d'une réservation..."
curl -s -X POST "$BASE_URL/reservations?userId=1&eventId=1&venueId=1" \
  -H "Content-Type: application/json" | json_pp

echo ""
echo "6. Création d'un paiement..."
curl -s -X POST "$BASE_URL/payments?reservationId=1&amount=75.50&method=carte" \
  -H "Content-Type: application/json" | json_pp

echo ""
echo "=========================================="
echo "   TESTS TERMINÉS"
echo "=========================================="
