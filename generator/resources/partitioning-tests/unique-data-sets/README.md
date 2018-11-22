Tests that a profile that contains all unique values in its datasets can be correctly partitioned

Profile Fields:
    Asset
    AssetType
    AccountType
    AccountLevel
    AuditLevel
    FundValue
    FundType

Expected Trees:
    1(Asset, AssetType)
    2(AccountType, AccountLevel)
    3(FundValue, FundType)
    4(AuditLevel)