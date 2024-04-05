package org.se13.view.ranking;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.se13.SE13Application;
import org.se13.sqlite.ranking.RankingRepository;
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.lifecycle.Lifecycle;
import org.se13.view.nav.Screen;


public class RankingScreenController extends BaseController {
    private int score; // 최종 점수
    private boolean isItem; // 아이템
    private String diff; // 난이도
    @FXML
    private Button homeButton;
    @FXML
    private Button tetrisButton;

    @FXML
    private TableView<Ranking> tableView;
    @FXML
    private TableColumn<Ranking, Number> positionColumn;
    @FXML
    private TableColumn<Ranking, String> nameColumn;
    @FXML
    private TableColumn<Ranking, Number> scoreColumn;
    @FXML
    private TableColumn<Ranking, Boolean> isItemColumn;
    @FXML
    private TableColumn<Ranking, String> diffColumn;

    @FXML
    private void handleHomeButtonAction() {
        // Turn into a start screen
        SE13Application.navController.navigate(Screen.START);
    }

    @FXML
    private void handleTetrisButtonAction() {
        // Turn into a tetris screen
        SE13Application.navController.navigate(Screen.TETRIS);
    }

    @Override
    public void onCreate() {
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        isItemColumn.setCellValueFactory(new PropertyValueFactory<>("isItem"));
        diffColumn.setCellValueFactory(new PropertyValueFactory<>("diff"));
        int currentScore = score;
        int lastRankingScore = getLastRankingScore();

        if (currentScore > lastRankingScore) {
            showNicknameInputUI(); // 닉네임 입력 화면을 표시하는 메서드
            loadRanking();
        } else {
            loadRanking(); // 바로 랭킹 화면을 로드하는 메서드
        }
    }

    @Override
    public void onStart() {
        // Do nothing
    }


    // 현재 점수를 설정하기 위한 메서드
    public void setArguments(int score, boolean isItem, String diff) {
        this.score = score;
        this.isItem = isItem;
        this.diff = diff;
    }

    private int getLastRankingScore() {
        RankingRepository rankingRepository = new RankingRepositoryImpl();
        // 랭킹 데이터 가져오기
        List<Map<String, Object>> rankingData = rankingRepository.getRanking();
        if (rankingData == null || rankingData.isEmpty()) {
            return 0; // 랭킹 데이터가 없는 경우
        }
        // 랭킹 데이터가 10개 미만인 경우
        if (rankingData.size() < 10) {
            return 0; // 랭킹 데이터가 10개 미만인 경우
        }
        // 마지막 랭킹 항목의 점수를 가져오기
        Object scoreObject = rankingData.getLast().get("score");
        if (scoreObject instanceof Number) {
            return ((Number) scoreObject).intValue();
        } else {
            return 0; // 점수 데이터가 유효하지 않는 경우
        }
    }

    private void showNicknameInputUI() {
        // TextInputDialog 생성
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("닉네임 입력");
        dialog.setHeaderText(null); // 헤더 텍스트는 사용하지 않음
        dialog.setContentText("닉네임을 입력해주세요:");

        // 사용자가 OK 버튼을 누를 때까지 대화 상자를 표시하고 입력 결과를 기다림
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nickname -> {
            // 랭킹 테이블에 인서트
            RankingRepository rankingRepository = new RankingRepositoryImpl();
            rankingRepository.insertRanking(nickname, score, isItem, diff);
            // 랭킹 데이터 로드
            List<Map<String, Object>> rankingData = rankingRepository.getRanking();
            // id가 autoincrement로 설정되어 있으므로 마지막 id를 가져옴
            int lastId = rankingData.stream().mapToInt(e -> (int) e.get("id")).max().orElse(0);
            tableView.setRowFactory(tv -> new TableRow<Ranking>() {
                @Override
                protected void updateItem(Ranking item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && item.getId() == lastId) {
                        // 조건을 만족하는 row의 배경색을 변경
                        setStyle("-fx-background-color: yellow;");
                    } else {
                        setStyle("");
                    }
                }
            });
        });
    }

    public void loadRanking() {
        // 랭킹 데이터 로드
        RankingRepository rankingRepository = new RankingRepositoryImpl();
        List<Map<String, Object>> rankingData = rankingRepository.getRanking();

        ObservableList<Ranking> rankings;
        if (rankingData != null && !rankingData.isEmpty()) {
            rankings = convertToRankingsList(rankingData);
        } else {
            // 데이터가 없는 경우 빈 ObservableList를 설정
            rankings = FXCollections.observableArrayList();
        }
        tableView.setItems(rankings);
    }


    private ObservableList<Ranking> convertToRankingsList(List<Map<String, Object>> rawData) {
        ObservableList<Ranking> rankings = FXCollections.observableArrayList();
        int rank = 1; // 순위를 나타내는 변수 초기화

        for (Map<String, Object> entry : rawData) {
            if (entry.get("name") == null || entry.get("score") == null) {
                continue;
            }
            int id = (Integer) entry.get("id");
            String name = (String) entry.get("name");
            int score = (Integer) entry.get("score");
            boolean isItem = (Boolean) entry.get("isItem");
            String diff = (String) entry.get("diff");
            rankings.add(new Ranking(id, rank, name, score, isItem, diff));
            rank++; // 다음 순위로 업데이트
        }

        return rankings;
    }
}
