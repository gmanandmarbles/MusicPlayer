import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {

    private static JFrame frame; // Declare frame as static

    private JLabel currentlyPlayingLabel;
    private JLabel songLabel;
    private JList<Song> songList; // Use custom model with cover art
    private DefaultListModel<Song> queueListModel;
    private Song selectedSong; // Store the selected song outside ActionListener

    public MusicPlayer() {
        frame = new JFrame("Music Player"); // Assign to static variable
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        currentlyPlayingLabel = new JLabel("Currently Playing:");
        songLabel = new JLabel("");

        List<Song> songs = new ArrayList<>(); // Store songs with cover art
        songs.add(new Song(1, "Song 1", "path_to_cover_art_1.jpg", 10)); // Add duration in seconds
        songs.add(new Song(2, "Song 2", "path_to_cover_art_2.jpg", 20));
        songs.add(new Song(3, "Song 3", "path_to_cover_art_3.jpg", 15));
        songs.add(new Song(4, "Song 4", "path_to_cover_art_4.jpg", 25));

        DefaultListModel<Song> songListModel = new DefaultListModel<>();
        for (Song song : songs) {
            songListModel.addElement(song);
        }

        songList = new JList<>(songListModel);
        songList.setCellRenderer(new SongListRenderer()); // Custom cell renderer to display cover art
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedSong = songList.getSelectedValue(); // Store the selected song
                if (selectedSong != null) {
                    // Call backend to start playing the selected song
                    songLabel.setText(selectedSong.getName());
                    Timer timer = new Timer(selectedSong.getDuration() * 1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            playNextSong(selectedSong.getId());
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a song to play.");
                }
            }
        });

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call backend to stop playing the current song
                songLabel.setText("");
            }
        });

        JButton addToQueueButton = new JButton("Add to Queue");
        addToQueueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Song selectedSong = songList.getSelectedValue();
                if (selectedSong != null) {
                    queueListModel.addElement(selectedSong);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a song to add to the queue.");
                }
            }
        });

        queueListModel = new DefaultListModel<>();
        JList<Song> queueList = new JList<>(queueListModel);
        queueList.setCellRenderer(new SongListRenderer()); // Custom cell renderer to display cover art
        queueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel songPanel = new JPanel();
        songPanel.setLayout(new BorderLayout());
        songPanel.add(currentlyPlayingLabel, BorderLayout.NORTH);
        songPanel.add(songLabel, BorderLayout.CENTER);

        JPanel songListPanel = new JPanel();
        songListPanel.setLayout(new BorderLayout());
        songListPanel.add(new JScrollPane(songList), BorderLayout.CENTER);
        songListPanel.add(addToQueueButton, BorderLayout.SOUTH);

        JPanel queuePanel = new JPanel();
        queuePanel.setLayout(new BorderLayout());
        queuePanel.add(new JScrollPane(queueList), BorderLayout.CENTER);
        queuePanel.add(playButton, BorderLayout.NORTH);
        queuePanel.add(stopButton, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(songListPanel);
        mainPanel.add(queuePanel);

        frame.add(songPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusicPlayer();
            }
        });
    }

    private void playNextSong(int currentSongId) {
        // Remove the played song from the queue
        queueListModel.removeElement(selectedSong);
        
        // If there are songs remaining in the queue, play the next one
        if (queueListModel.size() > 0) {
            Song nextSong = queueListModel.getElementAt(0);
            selectedSong = nextSong;
            songLabel.setText(selectedSong.getName());
            Timer timer = new Timer(selectedSong.getDuration() * 1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    playNextSong(selectedSong.getId());
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // No songs left in the queue, stop playback
            songLabel.setText("");
            JOptionPane.showMessageDialog(frame, "Queue is empty. Playback stopped.");
        }
    }

    // Custom class to represent a song with cover art and duration
    private class Song {
        private int id;
        private String name;
        private String coverArtPath;
        private int duration; // Duration in seconds

        public Song(int id, String name, String coverArtPath, int duration) {
            this.id = id;
            this.name = name;
            this.coverArtPath = coverArtPath;
            this.duration = duration;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCoverArtPath() {
            return coverArtPath;
        }

        public int getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Custom cell renderer to display cover art in the JList
    private class SongListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Song song = (Song) value;
            label.setIcon(new ImageIcon(song.getCoverArtPath())); // Set cover art as icon
            label.setHorizontalTextPosition(JLabel.RIGHT); // Adjust text position
            return label;
        }
    }
}
