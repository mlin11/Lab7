package lab7;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AminoAcidQuizGUI extends JFrame
{
	private static final long serialVersionUID = 37940599221211L;
	private JLabel fullLabel = new JLabel("Amino Acid Full Name");
	private JTextField fullCode = new JTextField();
	private JLabel oneLabel = new JLabel("Type One Letter Code and Enter");
	private JTextField oneCode = new JTextField();
	private JLabel timeLabel = new JLabel("30s");
	private JLabel outLabel = new JLabel("Right/Wrong: 0/0");
	private JButton startButton = new JButton("start quiz");
	private JButton cancelButton = new JButton("cancel");
	private volatile int countR = 0;
	private volatile int countW = 0;
	private final int remainingTime = 30;
	private long timeOut;
	private volatile boolean submitted;
	private volatile String target;
	private volatile String right;
	private volatile int n;

	public static String[] SHORT_NAMES = { "A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P",
			"S", "T", "W", "Y", "V" };
	public static String[] FULL_NAMES = { "alanine", "arginine", "asparagine", "aspartic acid", "cysteine", "glutamine",
			"glutamic acid", "glycine", "histidine", "isoleucine", "leucine", "lysine", "methionine", "phenylalanine",
			"proline", "serine", "threonine", "tryptophan", "tyrosine", "valine" };

	public AminoAcidQuizGUI()
	{
		super("Amino Acid Quiz");
		setSize(200, 200);
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
		panel.add(fullLabel);
		panel.add(fullCode);
		panel.add(oneLabel);
		oneCode.addActionListener(new EnterListener());
		panel.add(oneCode);
		timeLabel.setFont(new Font("Courier New", Font.BOLD, 20));
		panel.add(timeLabel);
		panel.add(outLabel);
		return panel;
	}

	private class EnterListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			oneCode.setText(oneCode.getText().toUpperCase());
			submitted = true;
		}
	}

	private class StartActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (startButton.getText() == "restart")
			{
				startButton.setText("start");
				startButton.setEnabled(false);
				cancelButton.setEnabled(true);
				countR = 0;
				countW = 0;
				new Thread(new typeInRunnable()).start();
				new Thread(new timeKeepRunnable()).start();

			} else
			{
				startButton.setEnabled(false);
				cancelButton.setEnabled(true);
				new Thread(new typeInRunnable()).start();
				new Thread(new timeKeepRunnable()).start();
			}

		}

	}

	private class CancelActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}

	}

	private class typeInRunnable implements Runnable
	{
		public void run()
		{
			try
			{
				submitted = false;
				oneCode.setEnabled(true);
				outLabel.setText("Right/Wrong: 0/0");
				timeOut = System.currentTimeMillis() + (remainingTime * 1000);
				while (System.currentTimeMillis() < timeOut)
				{
					Random random = new Random();
					n = random.nextInt(20);
					target = FULL_NAMES[n];
					right = SHORT_NAMES[n];
					fullCode.setText(target);
					// wait for the input
					while (!submitted)
						Thread.yield();
					if (oneCode.getText().equals(right))
					{
						countR = countR + 1;
					} else
					{
						countW = countW + 1;
					}
					outLabel.setText("Right/Wrong: " + countR + "/" + countW);
					oneCode.setText("");
					submitted = false;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}

	}

	private class timeKeepRunnable implements Runnable
	{
		public void run()
		{
			for (int x = 30; x >= 0; x--)
			{
				timeLabel.setText(x + "s");
				timeLabel.setFont(new Font("Courier New", Font.BOLD, 20));
				try
				{
					Thread.sleep(1000);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			timeLabel.setText("Game over!");
			oneCode.setEnabled(false);
			fullCode.setText("");
			startButton.setText("restart");
			startButton.setEnabled(true);
		}
	}

	public static void main(String[] args)
	{
		new AminoAcidQuizGUI();
	}
}
