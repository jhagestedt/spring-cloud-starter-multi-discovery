package org.springframework.cloud.extension.tuple;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Pair<L, R> implements Serializable {

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public final L left;
    public final R right;

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

}
