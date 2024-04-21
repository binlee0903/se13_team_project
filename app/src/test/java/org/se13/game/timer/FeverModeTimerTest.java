package org.se13.game.timer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class FeverModeTimerTest {
    private boolean isExecute = false;
    private boolean isRelease = false;
    final AtomicLong currentTime = new AtomicLong(0);
    long seconds = 1000000000;
    long half = seconds / 2;

    Runnable execute = () -> {
        isExecute = true;
        isRelease = false;
    };

    Runnable release = () -> {
        isExecute = false;
        isRelease = true;
    };

    FeverModeTimer timer = new FeverModeTimer(execute, release) {
        @Override
        protected long currentTime() {
            return currentTime.get();
        }
    };

    @Test
    @DisplayName("피버 모드 테스트")
    void test() {

        // 초기 상태는 아무것도 실행이 되어있지 않아야 합니다.
        assertTimer(false, false, false, false);

        // 피버모드 실행
        timer.execute(seconds + half);
        assertTimer(true, false, true, true);

        // 1초간 시간 진행
        currentTime.addAndGet(seconds);
        assertTimer(true, false, true, true);

        // 2초간 시간 정지
        timer.setPause();
        currentTime.addAndGet(2 * seconds);
        assertTimer(true, false, true, false);

        // 일시정지 해제
        timer.setResume();
        assertTimer(true, false, true, true);

        // 1초 시간 진행
        currentTime.addAndGet(seconds);
        assertTimer(true, false, true, false);

        // 피버 모드 해제
        timer.release();
        assertTimer(false, true, false, false);

        // 다시 실행
        timer.execute();
        assertTimer(true, false, true, true);
    }

    private void assertTimer(boolean execute, boolean release, boolean isActive, boolean isFever) {
        assertEquals(execute, isExecute);
        assertEquals(release, isRelease);
        assertEquals(isActive, timer.isActive());
        assertEquals(isFever, timer.isFeverMode());
    }
}