package transaction.properties;

import java.util.Objects;

public class Category {
    protected String emoji;
    protected String name;
    protected int budget;

    public Category(String emoji, String name, int budget) {
        this.emoji = emoji;
        this.name = name;
        this.budget = budget;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return budget == other.budget && Objects.equals(emoji, other.emoji) && Objects.equals(name, other.name);
	}
    
}
