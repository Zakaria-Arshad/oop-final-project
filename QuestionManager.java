import java.util.ArrayList;
import java.util.List;

public class QuestionManager {
    private List<Question> questions;
    private FileManager fileManager;

    public QuestionManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        questions.add(question);
        // DEBUG PRINT, CAN DELETE IT 
        System.out.println(questions);
        saveQuestions();
    }

    public void editQuestion(Question oldQuestion, Question updatedQuestion) {
        int index = questions.indexOf(oldQuestion);
        if (index != -1) {
            questions.set(index, updatedQuestion);
        }
        saveQuestions();
    }

    public void deleteQuestion(Question question) {
        questions.remove(question);
        saveQuestions();
    }

    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    public List<Question> filterQuestionsByCompletion(boolean completed) {
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question q : questions) {
            if (q.isCompleted() == completed) {
                filteredQuestions.add(q);
            }
        }
        return filteredQuestions;
    }
    
    public List<Question> filterQuestionsByType(String type) {
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question q : questions) {
            if (q.getDataStructureType().equalsIgnoreCase(type)) {
                filteredQuestions.add(q);
            }
        }
        return filteredQuestions;
    }
    

    public void loadQuestions() {
        questions = fileManager.loadQuestions();
    }

    public void saveQuestions() {
        fileManager.saveQuestions(questions);
    }
}

