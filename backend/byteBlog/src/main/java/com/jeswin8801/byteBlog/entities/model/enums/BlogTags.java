package com.jeswin8801.byteBlog.entities.model.enums;

import lombok.Getter;

@Getter
public enum BlogTags {
    TUTORIAL("Tutorial"),
    GUIDE("Guide"),
    REVIEW("Review"),
    OPINION("Opinion"),
    NEWS("News"),
    UPDATE("Update"),
    ANNOUNCEMENT("Announcement"),
    HOW_TO("How-To"),
    BEST_PRACTICES("Best Practices"),
    TIPS_AND_TRICKS("Tips and Tricks"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    JAVA("Java"),
    C_HASH("C#"),
    CPP("C++"),
    RUBY("Ruby"),
    GO("Go"),
    PHP("PHP"),
    SWIFT("Swift"),
    KOTLIN("Kotlin"),
    REACT("React"),
    ANGULAR("Angular"),
    VUE_JS("Vue.js"),
    DJANGO("Django"),
    SPRING_BOOT("Spring Boot"),
    EXPRESS_JS("Express.js"),
    FLASK("Flask"),
    LARAVEL("Laravel"),
    NODE_JS("Node.js"),
    SYMFONY("Symfony"),
    AWS("AWS"),
    AZURE("Azure"),
    GOOGLE_CLOUD("Google Cloud"),
    DOCKER("Docker"),
    KUBERNETES("Kubernetes"),
    GIT("Git"),
    CI_CD("CI/CD"),
    JENKINS("Jenkins"),
    TENSORFLOW("TensorFlow"),
    PYTORCH("PyTorch"),
    AGILE("Agile"),
    SCRUM("Scrum"),
    MICROSERVICES("Microservices"),
    DEVOPS("DevOps"),
    CLOUD_COMPUTING("Cloud Computing"),
    MACHINE_LEARNING("Machine Learning"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence"),
    DATA_SCIENCE("Data Science"),
    BLOCKCHAIN("Blockchain"),
    CYBERSECURITY("Cybersecurity"),
    GITHUB("GitHub"),
    STACK_OVERFLOW("Stack Overflow"),
    MEDIUM("Medium"),
    REDDIT("Reddit"),
    TWITTER("Twitter"),
    LINKEDIN("LinkedIn"),
    YOUTUBE("YouTube"),
    TWITCH("Twitch"),
    DISCORD("Discord");

    private final String tag;

    BlogTags(String tag) {
        this.tag = tag;
    }
}
