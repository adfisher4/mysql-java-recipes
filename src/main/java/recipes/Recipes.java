package recipes;

import java.sql.Connection;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.dao.DbConnection;
import recipes.entity.Recipe;
import recipes.exception.DbException;
import recipes.service.RecipeService;

public class Recipes {
	
	private Scanner scanner = new Scanner(System.in);
	private RecipeService recipeService = new RecipeService();
	private Recipe curRecipe; // ADDED ************************************************
	private List<String> operations = List.of(
			"1) Create and populate all tables",
			"2) Add a recipe",
			"3) List recipe",
			"4) Select working recipe");

	public static void main(String[] args) {
		new Recipes().displayMenu();
		
		//DbConnection.getConnection();
		

	}
	
	private void displayMenu() {
		boolean done = false;
		
		while (!done) {
			
			try {
			int operation = getOperation();
			
			switch (operation) {
			case -1:
				done = exitMenu();
				break;
			
			case 1:
				createTables();
				break;
				
			case 2:
				addRecipe();
				break;
				
			case 3:
				listRecipes();
				break;
			case 4:
				setCurrentRecipe();
				break;
			default:
				System.out.println("\n" + operation + " is not valid. Try again.");
			
		}
			} catch(Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
	}
	}
		
	private void setCurrentRecipe() {
		
		List<Recipe> recipes = listRecipes();
		
		Integer recipeId = getIntInput("Select a recipe ID");
		
		curRecipe = null;
		
		for(Recipe recipe: recipes) {
			if(recipe.getRecipeId().equals(recipeId)) {
				curRecipe = recipeService.fetchRecipeById(recipeId);
				System.out.println(curRecipe); // ADDED  ***************************
				break;
			}
		}
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nInvalid recipe selected. ");
		}
	}

	private List<Recipe> listRecipes() {
		List<Recipe> recipes = recipeService.fetchRecipes();
		
		System.out.println("\nRecipes");
		
		recipes.forEach(recipe -> System.out.println("    " + recipe.getRecipeId() + ": " + recipe.getRecipeName()));
		return recipes;
	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name: ");
		String notes = getStringInput("Enter recipe notes: ");
		Integer numServings = getIntInput("Enter number of servings: ");
		Integer prepMinutes = getIntInput("Enter prep time in minutes: ");
		Integer cookMinutes = getIntInput("Enter cook time in minutes: ");
		
		LocalTime prepTime = minutesToLocalTime(prepMinutes);
		LocalTime cookTime = minutesToLocalTime(cookMinutes);
		
		Recipe recipe = new Recipe();
		
		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);
		
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe: \n" + dbRecipe);
		
		curRecipe = recipeService.fetchRecipeById(dbRecipe.getRecipeId());
	}

	private LocalTime minutesToLocalTime(Integer numMinutes) {
		int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
		int hours = min / 60 ;
		int minutes = min % 60;
		return LocalTime.of(hours, minutes);
	}

	private boolean exitMenu() {
			System.out.println("\nExiting the menu now.");
			return true;
		}
	
	private int getOperation() {
		printOperations();
		Integer op = getIntInput("Enter an operation number (Press Enter to quit)");
		
		return Objects.isNull(op) ? -1 : op;
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do: ");
		
		for (String op : operations) {
			System.out.println("   " + op);
		}
	}
	
	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}	
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}	
		

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();
		return line.isBlank() ? null : line.trim();
	}
		
	
	
	

	private void createTables() {
		recipeService.createAndPopulateTables();
		System.out.println("\nTables created and populated!");
		
		
	}


}
