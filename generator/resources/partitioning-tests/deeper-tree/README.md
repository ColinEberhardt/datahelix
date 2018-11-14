Tests that a tree with deeper decisions (2 layers rather than 1) can be correctly partitioned as expected.

Profile Fields:
    UserType
    PostType
    Post
    Action
    Topic
    Event
    Duration

Expected Trees:
    1(UserType, PostType, Post, Action)
    2(Topic, Event, Duration)