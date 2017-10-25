package lab7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AminoAcidQuizGUI extends JFrame
{
	private static final long serialVersionUID = 37940599221211L;
	private final JLabel fullLabel = new JLabel("Amino Acid Full Name");
	private final JTextArea fullCode = new JTextArea();
	private final JLabel oneLabel = new JLabel("Type One Letter Code and Enter");
	private final JTextField oneCode = new JTextField();
	private final JTextArea timeArea = new JTextArea("\n\n\n        Timer");
	private final JTextArea outArea = new JTextArea("\n\n\n  Right/Wrong: 0/0");
	private final JButton startButton = new JButton("start quiz");
	private final JButton cancelButton = new JButton("cancel");
	private volatile int countR = 0;
	private volatile int countW = 0;
	private volatile String target;
	private volatile String right;
	private volatile boolean submitted;
	private volatile boolean cancel;
	private volatile boolean stop;

	public static String[] SHORT_NAMES = { "A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P",
			"S", "T", "W", "Y", "V" };
	public static String[] FULL_NAMES = { "alanine", "arginine", "asparagine", "aspartic acid", "cysteine", "glutamine",
			"glutamic acid", "glycine", "histidine", "isoleucine", "leucine", "lysine", "methionine", "phenylalanine",
			"proline", "serine", "threonine", "tryptophan", "tyrosine", "valine" };

	public AminoAcidQuizGUI()
	{
		super("Amino Acid Quiz");
		setSize(700, 600);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);
		getContentPane().add(getTextPanel(), BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public JPanel getBottomPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		startButton.addActionListener(new StartActionListener());
		cancelButton.addActionListener(new CancelActionListener());
		panel.add(startButton);
		panel.add(cancelButton);
		return panel;
	}

	public JPanel getTextPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		fullLabel.setFont(new Font("Verdana", Font.BOLD, 18));
		fullLabel.setForeground(Color.BLACK);
		panel.add(fullLabel);
		fullCode.setFont(new Font("Verdana", Font.BOLD, 24));
		fullCode.setEnabled(false);
		panel.add(fullCode);
		oneLabel.setFont(new Font("Verdana", Font.BOLD, 18));
		oneLabel.setForeground(Color.BLACK);
		panel.add(oneLabel);
		oneCode.addActionListener(new EnterListener());
		panel.add(oneCode);
		timeArea.setFont(new Font("Verdana", Font.BOLD, 24));
		timeArea.setForeground(Color.BLUE);
		panel.add(timeArea);
		outArea.setFont(new Font("Verdana", Font.BOLD, 24));
		outArea.setForeground(Color.BLACK);
		panel.add(outArea);
		return panel;
	}

	// add listener to detect input when ENTER is pressed
	private class EnterListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			oneCode.setText(oneCode.getText().toUpperCase());
			submitted = true;
		}
	}

	// add listener to 'start quiz' button to start the quiz and enable the 'restart
	// button'
	private class StartActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (startButton.getText() == "restart")
			{
				stop = false;
				countR = 0;
				countW = 0;
				timeArea.setText("\\n\n\n        Timer");
				outArea.setText("\n\n\n  Right/Wrong: 0/0");
				oneCode.setEnabled(true);
				startButton.setEnabled(false);
				cancelButton.setText("cancel");
				cancelButton.setEnabled(true);
				Random random = new Random();
				int n = random.nextInt(20);
				target = FULL_NAMES[n];
				right = SHORT_NAMES[n];
				fullCode.setText("\n\n" + target);
				new Thread(new timeKeepRunnable()).start();
			} else
			{
				startButton.setEnabled(false);
				cancelButton.setEnabled(true);
				new Thread(new timeKeepRunnable()).start();
				new Thread(new checkRunnable()).start();
			}
		}
	}

	// add listener to cancel button and enable exit fucntion
	private class CancelActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (cancelButton.getText() == "exit")
			{
				System.exit(0);
			} else
			{
				cancel = true;
				startButton.setText("restart");
				startButton.setEnabled(true);
				stop = true;
				cancelButton.setEnabled(false);
			}
		}
	}

	// a thread for the amino acid quiz part by implementing runnable
	private class checkRunnable implements Runnable
	{
		public void run()
		{
			while (!stop)
			{
				try
				{
					Random random = new Random();
					int n = random.nextInt(20);
					target = FULL_NAMES[n];
					right = SHORT_NAMES[n];
					fullCode.setText("\n\n" + target);
					while (!submitted)
					{
						Thread.yield();
					}

				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						public void run()
						{
							if (oneCode.getText().equals(right))
							{
								countR = countR + 1;
							} else
							{
								countW = countW + 1;
							}
							outArea.setText("\n\n\n  Right/Wrong: " + countR + "/" + countW);
							oneCode.setText("");
							submitted = false;
						}

					});
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	// a thread for timer by implementing runnable
	private class timeKeepRunnable implements Runnable
	{
		public void run()
		{
			try
			{
				int startTime = 30;
				while (!cancel && startTime > 0)
				{
					timeArea.setText("\n\n\n        " + startTime + "s");
					startTime--;
					Thread.sleep(1000);
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						timeArea.setText("\n\n\n      Game over!");
						fullCode.setText("");
						startButton.setText("restart");
						startButton.setEnabled(true);
						cancelButton.setText("exit");
						cancelButton.setEnabled(true);
						cancel = false;
						stop = true;
					}
				});
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		new AminoAcidQuizGUI();
	}
}
