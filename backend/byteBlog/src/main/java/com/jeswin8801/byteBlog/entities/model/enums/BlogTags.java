package com.jeswin8801.byteBlog.entities.model.enums;

import lombok.Getter;

@Getter
public enum BlogTags {
    PRODUCTIVITY("Productivity"),
    C("C"),
    NEWS("News"),
    CODING("Coding"),
    JAVASCRIPT("Javascript"),
    PYTHON("Python"),
    CAREER("Career"),
    OAUTH2("Oauth2"),
    CONTINUOUS_INTEGRATION("Continuous Integration"),
    ANGULAR("Angular"),
    AWS("Aws"),
    WEB3("Web3"),
    TUTORIAL("Tutorial"),
    GO("Go"),
    AI_ASSISTANT("Ai Assistant"),
    REVIEW("Review"),
    OPEN_SOURCE("Open Source"),
    CLOUD_COMPUTING("Cloud Computing"),
    KAFKA("Kafka"),
    ARCHITECTURE("Architecture"),
    PHP("Php"),
    GOOGLE_CLOUD("Google Cloud"),
    SUSTAINABILITY("Sustainability"),
    TECH_DEBT("Tech Debt"),
    DEVOPS("Devops"),
    LLM("Llm"),
    DEVELOPER_EXPERIENCE("Developer Experience"),
    ANNOUNCEMENTS("Announcements"),
    DDOS("Ddos"),
    EDGE_COMPUTING("Edge Computing"),
    COMPARISON("Comparison"),
    TIME_TRAVEL("Time Travel"),
    RETRIEVAL_AUGMENTED_GENERATION("Retrieval Augmented Generation"),
    DJANGO("Django"),
    RUBY("Ruby"),
    DOCKER("Docker"),
    PULL_REQUESTS("Pull Requests"),
    DATA("Data"),
    DATABASE("Database"),
    ACCESSIBILITY("Accessibility"),
    PRIVACY("Privacy"),
    JAVA("Java"),
    HOW_TO("How To"),
    VECTOR_SEARCH("Vector Search"),
    KUBERNETES("Kubernetes"),
    RESUME("Resume"),
    PLATFORM_ENGINEERING("Platform Engineering"),
    SECURITY("Security"),
    VUE("Vue"),
    REACT("React"),
    NODE_JS("Node Js"),
    AZURE("Azure"),
    BLOCKCHAIN("Blockchain"),
    AI("Ai"),
    CAREER_ADVICE("Career Advice"),
    GENERATIVE_AI("Generative Ai"),
    TIPS_AND_TRICKS("Tips And Tricks"),
    TENSORFLOW("Tensorflow"),
    SPRING_BOOT("Spring Boot"),
    GIT("Git");

    private final String tag;

    BlogTags(String tag) {
        this.tag = tag;
    }
}
