import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class BrowseGUI {
    private JTable questionsTable;
    private JComboBox<String> filterComboBox;
    private JComboBox<String> dataStructureFilterComboBox;
    private JCheckBox completionFilterCheckBox;
    private JButton refreshButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;

    private LeetCodeTrackerSystem system;

    public BrowseGUI(LeetCodeTrackerSystem system) {
        this.system = system;

        // get all questions
        QuestionManager questionManager = system.getQuestionManager();
        List<Question> questions = questionManager.getAllQuestions();
        // DEBUG PRINT
        System.out.println(questions);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Title");
        tableModel.addColumn("URL");
        tableModel.addColumn("Type");
        tableModel.addColumn("Difficulty");

        // populate table with questions
        for (Question question : questions) {
            tableModel.addRow(new Object[] {
                question.getTitle(),
                question.getUrl(),
                question.getDataStructureType(),
                question.getDifficulty()
            });
        }

        questionsTable = new JTable(tableModel);

        // filters
        filterComboBox = new JComboBox<>(new String[]{"All", "Easy", "Medium", "Hard"});
        dataStructureFilterComboBox = new JComboBox<>(getDataStructureTypes(questions));
        completionFilterCheckBox = new JCheckBox("Show Completed");
        refreshButton = new JButton("Refresh");
        editButton = new JButton("Edit Question");
        deleteButton = new JButton("Delete Question");
        backButton = new JButton("Back");

        initUI();
        addActionListeners();
    }

    private String[] getDataStructureTypes(List<Question> questions) {
        // get the specific data structure types
        List<String> types = new ArrayList<>();
        types.add("All");
        for (Question question : questions) {
            if (!types.contains(question.getDataStructureType())) {
                types.add(question.getDataStructureType());
            }
        }
        return types.toArray(new String[0]);
    }

    private void initUI() {
        JFrame frame = new JFrame("Browse Questions");
        JPanel panel = new JPanel();

        panel.add(filterComboBox);
        panel.add(dataStructureFilterComboBox);
        panel.add(completionFilterCheckBox);
        panel.add(refreshButton);
        panel.add(new JScrollPane(questionsTable));
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(backButton);

        frame.add(panel);
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void refreshQuestionsTable() {
        // updated qs
        List<Question> allQuestions = system.getQuestionManager().getAllQuestions();

        // filters
        String selectedDifficulty = (String) filterComboBox.getSelectedItem();
        String selectedDataStructure = (String) dataStructureFilterComboBox.getSelectedItem();
        boolean showCompleted = completionFilterCheckBox.isSelected();

        // apply the filters
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question question : allQuestions) {
            if ((selectedDifficulty.equals("All") || question.getDifficulty().equalsIgnoreCase(selectedDifficulty)) &&
                (selectedDataStructure.equals("All") || question.getDataStructureType().equalsIgnoreCase(selectedDataStructure)) &&
                (!showCompleted || question.isCompleted())) {
                filteredQuestions.add(question);
            }
        }

        // update the table
        DefaultTableModel tableModel = (DefaultTableModel) questionsTable.getModel();
        tableModel.setRowCount(0); // Reset table
        for (Question question : filteredQuestions) {
            tableModel.addRow(new Object[]{
                question.getTitle(),
                question.getUrl(),
                question.getDataStructureType(),
                question.getDifficulty()
            });
        }
    }

    private void addActionListeners() {
        refreshButton.addActionListener(_ -> refreshQuestionsTable());

        filterComboBox.addActionListener(_ -> refreshQuestionsTable());

        dataStructureFilterComboBox.addActionListener(_ -> refreshQuestionsTable());

        completionFilterCheckBox.addActionListener(_ -> refreshQuestionsTable());

        editButton.addActionListener(_ -> {
            Question selectedQuestion = getSelectedQuestion();
            if (selectedQuestion != null) {
                new EditGUI(system, selectedQuestion);
            }
        });

        deleteButton.addActionListener(_ -> {
            Question selectedQuestion = getSelectedQuestion();
            if (selectedQuestion != null) {
                new DeleteGUI(system, selectedQuestion);
            }
        });

        backButton.addActionListener(_ -> ((JFrame) SwingUtilities.getWindowAncestor(backButton)).dispose());
    }

    private Question getSelectedQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel tableModel = (DefaultTableModel) questionsTable.getModel();
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            String url = (String) tableModel.getValueAt(selectedRow, 1);

            // get questions and find the selected one
            QuestionManager questionManager = system.getQuestionManager();
            List<Question> questions = questionManager.getAllQuestions();

            for (Question question : questions) {
                if (question.getTitle().equals(title) && question.getUrl().equals(url)) {
                    return question;
                }
            }
        }
        return null;
    }
}


