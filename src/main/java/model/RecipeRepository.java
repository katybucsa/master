package model;


import java.util.*;

public class RecipeRepository {

    private final Map<Integer, Recipe> recipes;
    private static RecipeRepository instance=null;

    static public RecipeRepository getInstance() {

        if (instance == null)
            instance = new RecipeRepository();

        return instance;
    }

    private RecipeRepository() {

        recipes = new HashMap<>();
        initRepo();
    }

    private void initRepo() {

        Recipe recipe1 = new Recipe(1, "Prajitura cu visine",
                new ArrayList<>(Arrays.asList("4 oua", "100 g zahar", "4 linguri cu varf faina",
                        "2 linguri ulei", "1 plic praf de copt", "2 plicuri zahar vanilat",
                        "un praf sare", "300 g visine proaspete")),
                "Visinele se spala, se scot samburii si se pun la scurs intr-o sita.\n" +
                        "Albusurile se bat spuma tare cu un praf de sare.\n" +
                        "Se adauga zaharul si zaharul vanilat si se mixeaza pana se obtine o bezea lucioasa.\n" +
                        "Se amesteca galbenusurile cu uleiul si se toarna peste bezea.\n" +
                        "Daca bezeaua este batuta bine amestecul trebuie sa stea la suprafata.\n" +
                        "Se incorporeaza usor.\n" +
                        "Faina se amesteca cu praful de copt, se pune peste bezea si se incorporeaza usor cu o spatula de sus in jos.\n" +
                        "Se tapeteaza o tava (diametru 24 cm) cu hartie de copt si se toarna compozitia.\n" +
                        "Visinele se presara cu putina faina si se pun deasupra in tava.\n" +
                        "Se introduce in cuptorul preincalzit la foc mediu pana se rumeneste usor deasupra (20-25 minute).\n" +
                        "Se lasa la racit in tava.\n" +
                        "Se scoate pe platou si se portioneaza.\n" +
                        "Se poate presara cu zahar pudra.\n" +
                        "Pofta Buna!", 2, 60);

        Recipe recipe2 = new Recipe(2, "Tort de inghetata",
                new ArrayList<>(Arrays.asList("250 g capsuni",
                        "500 ml smantana pentru frisca",
                        "500 g mascarpone",
                        "30 g cacao",
                        "200 g zahar pudra",
                        "1 lingura esenta de vanilie")),
                "Topeste ciocolata neagra si ciocolata cu lapte impreuna, la bain marie.\n" +
                        "Alege tava in care urmeaza sa asamblezi tortul de inghetata. Tava trebuie sa fie inalta si ingusta, similara cu cea de cozonac, eventual putin mai lata.\n" +
                        "Intinde o foaie de copt pe blat si deseneaza de mai multe ori (6-7 dreptunghiuri) cu creionul conturul tavii alese, asezand tava normal, nu cu gura in jos. Odata ce ciocolata s-a topit, ia-o de pe foc si amesteca bine pentru omogenizare.\n" +
                        "Toarna putina ciocolata pe fiecare dreptunghi desenat si intinde ciocolata in strat subtire, dar consistent, pe intreaga suprafata marcata. Repeta procedeul pe toate dreptunghiurile desenate, sau pana ramai fara ciocolata.\n" +
                        "Lasa foaia de copt pe blat pentru ca ciocolata sa se intareasca putin, apoi da-o la frigider, dupa ce esti sigur ca nu se va deplasa.\n" +
                        "In tava in care urmeaza sa asamblezi tortul de inghetata Vienneta, intinde folie alimentara de plastic, lasand margini in exterior. Intinde un strat de inghetata (o poti lasa afara cu putin timp inainte, pentru a fi mai maleabila). Dupa primul strat de inghetata, asaza o foaie de ciocolata din cele date la frigider.\n" +
                        "Repeta apoi procedeul: intinde inghetata peste, urmata de o alta foaie de ciocolata. In final, stratul care ramane deasupra trebuie sa fie de inghetata.\n" +
                        "Marunteste biscuitii cat mai fin in robotul de bucatarie sau folosind facaletul. Combina-i cu 2 linguri de unt topit si o lingura de zahar. Intinde amestecul crocant obtinut deasupra intregului tort de inghetata. Da tortul la congelator pana cand decorul e gata.\n" +
                        "Bate frisca cu mascarpone si zaharul pudra. Pune crema intr-un pos si scoate tortul din forma, intorcandu-l invers pe platoul de servire. Elimina folia alimentara si orneaza tortul pe partea de sus, formand linii groase de crema, una langa cealalta, iar pe laterale cu onduleuri de crema.\n" +
                        "Presara cacao deasupra si da direct tortul la congelator, sau da intai tortul la congelator pentru minim o ora, apoi toarna ciocolata topita si racita deasupra.", 3, 90);

        addRecipe(recipe1);
        addRecipe(recipe2);
    }

    public void addRecipe(Recipe recipe) {

        this.recipes.put(recipe.getId(), recipe);
    }

    public Recipe getById(int id) {

        return recipes.get(id);
    }

    public List<Recipe> getAll() {

        return new ArrayList<>(this.recipes.values());
    }

    public int getLastId() {

        return ((Recipe) recipes.values().toArray()[recipes.size() - 1]).getId();
    }
}
