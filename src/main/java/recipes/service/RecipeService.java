package recipes.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import recipes.dao.RecipeDao;
import recipes.entity.Recipe;
import recipes.exception.DbException;

public class RecipeService {
	private static final String SCHEMA_FILE = "recipe_schema.sql";
	private static final String DATA_FILE = "recipe_data.sql";
	private RecipeDao recipeDao = new RecipeDao();
	
	
	public Recipe fetchRecipeById(Integer recipeId) {
		return recipeDao.fetchRecipeById(recipeId)
				.orElseThrow(() -> new NoSuchElementException(
				"Recipe with ID=" + recipeId + " does not exist."));
	}
	
	public void createAndPopulateTables() {
		loadFromFile(SCHEMA_FILE);
		loadFromFile(DATA_FILE);
		System.out.println("Data done");
	}
	
	public void queryTables() {
		
	}
	
	

	private void loadFromFile(String fileName) {
		String content = readFileContent(fileName);
		List<String> sqlStatements = convertContentToSqlStatements(content);
		
		//sqlStatements.forEach(line -> System.out.println(line));
		recipeDao.executeBatch(sqlStatements);
		
		
	}

	private List<String> convertContentToSqlStatements(String content) {
		content = removeComments(content);
		content = replaceWhiteSpaceSequencesWithSingleSpace(content);
		
		return extractLinesFromContent(content);

	}

	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<>();
		while (!content.isEmpty()) {
			int semicolon = content.indexOf(";");
			
			if (semicolon == -1) {
				if (!content.isBlank()) {
					lines.add(content);
				}
				
				content = "";
			} else {
				lines.add(content.substring(0, semicolon).trim());
				content = content.substring(semicolon + 1);
			}
		}
		return lines;
	}

	private String replaceWhiteSpaceSequencesWithSingleSpace(String content) {
		
		return content.replaceAll("//s+", " ");
	}

	private String removeComments(String content) {
		StringBuilder builder = new StringBuilder(content);
		int commentPos = 0;
		
		while ((commentPos = builder.indexOf("-- ", commentPos)) != -1) {
			int eolPos = builder.indexOf("\n", commentPos + 1);
			
			if (eolPos == -1) {
				builder.replace(commentPos, builder.length(), "");
			} else {
				builder.replace(commentPos, eolPos + 1, content);
			}
		}
		
		return builder.toString();
	}

	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
			return Files.readString(path);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}
	
	public Recipe addRecipe(Recipe recipe) {
		return recipeDao.insertRecipe(recipe);
	}
	// Potentially remove main method
//public static void main(String[] args) {
		//new RecipeService().createAndPopulateTables();
	//}

	public List<Recipe> fetchRecipes() {
		return recipeDao.fetchAllRecipes();
	}

}
