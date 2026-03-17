package com.openclassrooms.mdd_api.comment.repository;

/** Projection pour le nombre de commentaires par post (utilisée par le feed). */
public interface PostCommentCountRow {
    Long getPostId();
    long getCount();
}
