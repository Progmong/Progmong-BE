package com.progmong.api.user.entity;

import com.progmong.api.community.entity.Comment;
import com.progmong.api.community.entity.Liked;
import com.progmong.api.community.entity.Post;
import com.progmong.api.explore.entity.ProblemRecord;
import com.progmong.api.explore.entity.RecommendProblem;
import com.progmong.api.explore.entity.SolvedProblem;
import com.progmong.api.pet.entity.UserPet;
import com.progmong.api.tag.entity.InterestTag;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    private String nickname;

    @Column(nullable = false)
    private String password;

    private String bojId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterestTag> interestTags = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserPet userPet;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<SolvedProblem> solvedProblems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<ProblemRecord> problemRecords = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<RecommendProblem> recommendProblems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Liked> liked = new ArrayList<>();

    public void updatePassword(String password) {
        this.password = password;
    }
}
