Feature: Something
  Something something

  Scenario Outline: Published message <msg> goes back through listener from <channel>
    Given I have a pubnub instance
    When I subscribe to <channel>
    And I publish <msg> to <channel>
    Then On <channel> I should receive <msg>

    Examples:
      | msg                 | channel |
      | hey                 | ch1     |
      | hello               | ch2     |
      | is_it_me            | ch3     |
      | you're_looking_for? | ch4     |
    
