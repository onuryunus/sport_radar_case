package net.sympower.cityzen.apx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageLoader
{
  public static void main(String[] args) throws Exception
  {
    if (args.length == 0)
    {
      throw new Exception("Absolute file path must be declared at first argument");
    }

    String filePath = args[0];
    BufferedReader reader;
    try
    {
      reader = new BufferedReader(new FileReader(
              filePath));

      String line = null;
      while ((line = reader.readLine()) != null)
      {
        if (line.isBlank() || line.isEmpty())
        {
          continue;
        }

        String[] array = line.split(" ");

        List<Item> itemList = new ArrayList<>();
        for (int i = 2; i < array.length; i++) // Iteration must start at index 2 Because first data going to start after ":" mark.
        {
          String itemDataline = array[i];

          Pattern bracketsRegex = Pattern.compile("\\((.*?)\\)"); // Parsing items into brackets

          Matcher breacketsRegexMatcher = bracketsRegex.matcher(itemDataline);

          List<String> boxItems = new ArrayList<String>();
          while (breacketsRegexMatcher.find())
          {
            boxItems.add(breacketsRegexMatcher.group(1));
          }

          for (String boxItem : boxItems)
          {
            String[] itemDefinitionSplit = boxItem.split(","); // Parsing items values in brackets (index,weight,amount)
            int itemIndex = Integer.parseInt(itemDefinitionSplit[0]);
            double itemWeight = Double.parseDouble(itemDefinitionSplit[1]);
            double itemAmount = Double.parseDouble(itemDefinitionSplit[2].substring(1));

            if (itemWeight > 100) // Maximum item weight control
            {
              continue;
            }

            if (itemAmount > 100) // Maximum item amount control
            {
              continue;
            }

            itemList.add(new Item(itemIndex, itemWeight, itemAmount));
          }
        }

        List<Item> packageItemList = new ArrayList<>();
        findPackageItems(100, itemList, packageItemList, itemList.size());

        Package itemPackage = new Package(packageItemList);
        itemPackage.print();
      }

      reader.close();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * This method is used to find package item via basic Knapsack algorithm (Recursive method).
   * This can also be done with dynamic programming.
   *
   * @param weight        This is weight limit of package.
   * @param item          This is a list of package item options. (All items for selection)
   * @param optimalChoice This is result of package items.
   * @param n             All item list size
   * @return int This returns sum of numA and numB.
   */
  public static double findPackageItems(double weight, List<Item> item, List<Item> optimalChoice, int n)
  {
    if (n == 0 || weight == 0)
    {return 0;}

    if (item.get(n - 1).getWeight() > weight) // Recursive weight control. If the weight limit is exceeded the method is called again
    {
      List<Item> subOptimalChoice = new ArrayList<>();
      double optimalCost = findPackageItems(weight, item, subOptimalChoice, n - 1);
      optimalChoice.addAll(subOptimalChoice);
      return optimalCost;
    } else
    {
      List<Item> packageIncludeWithItem = new ArrayList<>();
      List<Item> packageExcludeWithItem = new ArrayList<>();

      // The amount including the item in the loop
      double itemIncludeCost = item.get(n - 1).getAmount() + findPackageItems((weight - item.get(n - 1).getWeight()), item, packageIncludeWithItem, n - 1);
      // The amount excluding the item in the loop
      double itemExcludeCost = findPackageItems(weight, item, packageExcludeWithItem, n - 1);

      // If the price with which the product is included is more than the price at which it is not, the product is selected to be included in the package.
      if (itemIncludeCost > itemExcludeCost)
      {
        optimalChoice.addAll(packageIncludeWithItem); // Recursive items according selected item.
        optimalChoice.add(item.get(n - 1)); // Item itself.
        return itemIncludeCost;
      } else
      {
        optimalChoice.addAll(packageExcludeWithItem);
        return itemExcludeCost;
      }
    }
  }

  public static class Item
  {
    private int index;

    private double weight;

    private double amount;

    public Item(int index, double weight, double amount)
    {
      this.index = index;
      this.weight = weight;
      this.amount = amount;
    }

    public int getIndex()
    {
      return index;
    }

    public void setIndex(int index)
    {
      this.index = index;
    }

    public double getWeight()
    {
      return weight;
    }

    public void setWeight(double weight)
    {
      this.weight = weight;
    }

    public double getAmount()
    {
      return amount;
    }

    public void setAmount(double amount)
    {
      this.amount = amount;
    }
  }

  public static class Package
  {
    private List<Item> items = new ArrayList<>();

    public Package(List<Item> items)
    {
      this.items = items;
    }

    public List<Item> getItems()
    {
      return items;
    }

    public void setItems(List<Item> items)
    {
      this.items = items;
    }

    public void print()
    {
      System.out.println(toString());
    }

    @Override
    public String toString()
    {
      if (items.size() == 0)
      {
        return "-";
      }

      StringBuilder lineResult = new StringBuilder();
      for (Item item : items)
      {
        lineResult.append(item.getIndex());
        lineResult.append(",");
      }

      if (!lineResult.toString().isEmpty() && !lineResult.toString().isBlank())
      {
        return lineResult.substring(0, lineResult.length() - 1);
      }

      return null;
    }
  }
}
