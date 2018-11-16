Tests that a tree that has daisy chaining rules can be partitioned to the expected number of trees. Daisy chaining occurs
when one rule depends on the value of another rule, which it itself depends on the value of another rule.

Profile Fields:
    FirstField
    SecondField
    ThirdField
    FourthField
    FifthField
    SixthField
    SeventhField
    EighthField

Expected Trees:
    1(FirstField, SecondField, ThirdField)
    2(FourthField, FifthField)
    3(SixthField, SeventhField, EighthField)