import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BrowseGUI {
    private JTable questionsTable;
    private JComboBox<String> filterComboBox;
    private JCheckBox completionFilterCheckBox;
    private JButton refreshButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;

    private LeetCodeTrackerSystem system;

    public BrowseGUI(LeetCodeTrackerSystem system) {
        this.system = system;

        // getting the quesitons
        QuestionManager questionManager = system.getQuestionManager();
        List<Question> questions = questionManager.getAllQuestions();
        // DEBUG PRINT
        System.out.println(questions);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Title");
        tableModel.addColumn("URL");
        tableModel.addColumn("Type");
        tableModel.addColumn("Difficulty");
        

        // adding the questions to table
        for (Question question : questions) {
            tableModel.addRow(new Object[] {
                question.getTitle(),
                question.getUrl(),
                question.getDataStructureType(),
                question.getDifficulty(),
              
            });
        }

        // Create the questionsTable
        questionsTable = new JTable(tableModel);

        filterComboBox = new JComboBox<>(new String[]{"All", "Easy", "Medium", "Hard"});
        completionFilterCheckBox = new JCheckBox("Show Completed");
        refreshButton = new JButton("Refresh");
        editButton = new JButton("Edit Question");
        deleteButton = new JButton("Delete Question");
        backButton = new JButton("Back");

        initUI();
        addActionListeners();
    }

    private void initUI() {
        JFrame frame = new JFrame("Browse Questions");
        JPanel panel = new JPanel();
    
        panel.add(filterComboBox);
        panel.add(completionFilterCheckBox);
        panel.add(refreshButton);
        panel.add(new JScrollPane(questionsTable));
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(backButton);
    
        frame.add(panel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void refreshQuestionsTable() {
        // gets updated questions (assuming something wrote to it so we need to refresh it)
        List<Question> allQuestions = system.getQuestionManager().getAllQuestions();
    
        // getting filters, if needed
        String selectedDifficulty = (String) filterComboBox.getSelectedItem();
        boolean showCompleted = completionFilterCheckBox.isSelected();
    
        // filter if needed (not will always be used)
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question question : allQuestions) {
            if ((selectedDifficulty.equals("All") || question.getDifficulty().equalsIgnoreCase(selectedDifficulty)) &&
                (!showCompleted || question.isCompleted())) {
                filteredQuestions.add(question);
            }
        }

    
        DefaultTableModel tableModel = (DefaultTableModel) questionsTable.getModel();
    
        // reset table
        tableModel.setRowCount(0);
    
        // add the new questions back now
        for (Question question : filteredQuestions) {
            tableModel.addRow(new Object[]{
                question.getTitle(),
                question.getUrl(),
                question.getDataStructureType(),
                question.getDifficulty(),
                
            });
        }
    }

    private void addActionListeners() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshQuestionsTable();
            }
        });

        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshQuestionsTable();
            }
        });
    
        completionFilterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshQuestionsTable();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Question selectedQuestion = getSelectedQuestion();
                if (selectedQuestion != null) {
                    new EditGUI(system, selectedQuestion);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Question selectedQuestion = getSelectedQuestion();
                if (selectedQuestion != null) {
                    new DeleteGUI(system, selectedQuestion);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JFrame) SwingUtilities.getWindowAncestor(backButton)).dispose();
            }
        });
    }

    private Question getSelectedQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel tableModel = (DefaultTableModel) questionsTable.getModel();
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            String url = (String) tableModel.getValueAt(selectedRow, 1);
            
    
            // get the questions again
            QuestionManager questionManager = system.getQuestionManager();
            List<Question> questions = questionManager.getAllQuestions();
    
            // look for the question we want
            for (Question question : questions) {
                if (question.getTitle().equals(title) && question.getUrl().equals(url)) {
                    return question;
                }
            }
        }
        return null;
    }
}

