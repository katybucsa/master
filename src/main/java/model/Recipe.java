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
    private int dificulty;
    private int preparingTime;

    public Recipe(String name, List<String> ingredients, String method, int dificulty, int preparingTime) {
        this.name = name;
        this.ingredients = ingredients;
        this.method = method;
        this.dificulty = dificulty;
        this.preparingTime = preparingTime;
    }
}
