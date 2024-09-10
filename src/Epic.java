import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void updateStatus(List<Subtask> subtasks) {
        if (subtaskIds.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;
        boolean anyNew = false;

        for (int subtaskId : subtaskIds) {
            for (Subtask subtask : subtasks) {
                if (subtask.getId() == subtaskId) {
                    if (subtask.getStatus() != TaskStatus.DONE) {
                        allDone = false;
                    }
                    if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                        anyInProgress = true;
                    }
                    if (subtask.getStatus() == TaskStatus.NEW) {
                        anyNew = true;
                    }
                }
            }
        }

        if (allDone) {
            setStatus(TaskStatus.DONE);
        } else if (anyInProgress || (anyNew && !allDone)) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateTiming(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            endTime = null;
            return;
        }

        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            Duration subtaskDuration = subtask.getDuration();

            if (subtaskStartTime != null) {
                if (earliestStartTime == null || subtaskStartTime.isBefore(earliestStartTime)) {
                    earliestStartTime = subtaskStartTime;
                }
                if (subtaskEndTime != null && (latestEndTime == null || subtaskEndTime.isAfter(latestEndTime))) {
                    latestEndTime = subtaskEndTime;
                }
            }

            if (subtaskDuration != null) {
                totalDuration = totalDuration.plus(subtaskDuration);
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStartTime);
        endTime = latestEndTime;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "id=" + getId() +
                ", название='" + getName() + '\'' +
                ", описание='" + getDescription() + '\'' +
                ", статус=" + getStatus() +
                ", подзадачи=" + subtaskIds +
                '}';
    }
}