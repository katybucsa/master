package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    private int id;
    private String name;
    private List<String> ingredients;
    private String method;
    private int dificultyRanking;
    private int preparingTime;
}
