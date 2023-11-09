Feature: Food management

  Rule: Adding new food

    Scenario: Adding new ingredient food
      Given User 'alpha' is present
      And I'm authenticated as 'alpha'
      And prepared request with following data:
        """json
        {
          "name": "Rice",
          "description": "Rice description",
          "pfcc": {
            "protein": 6.06,
            "fat": 0.53,
            "carbohydrates": 72.79,
            "calories": 330
          },
          "isHidden": false,
          "type": "INGREDIENT"
        }
        """
      When I'm sending POST request to "/api/food"
      Then I should receive successful response
      And Response should look like:
        """json
        {
          "success": true,
          "data": {
            "name": "Rice",
            "description": "Rice description",
            "pfcc": {
              "protein": 6.06,
              "fat": 0.53,
              "carbohydrates": 72.79,
              "calories": 330
            },
            "hidden": false,
            "type": "INGREDIENT",
            "ownedByUser": true
          }
        }
        """
      And the food is saved in db and contain next fields:
        | name               | Rice             |
        | description        | Rice description |
        | pfcc_protein       | 6.06             |
        | pfcc_fat           | 0.53             |
        | pfcc_carbohydrates | 72.79            |
        | pfcc_calories      | 330              |
        | is_hidden          | false            |
        | is_deleted         | false            |
        | type               | INGREDIENT       |
      And the food is owned by user 'alpha'
