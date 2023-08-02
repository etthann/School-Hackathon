package src.ai;

import java.util.ArrayList;

import src.GamePanel;
import src.tile.TileManager;

class Pathfinding {

    GamePanel gp;
    TileManager tileM;
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;

    public void PathFinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
    }

    public void instantiateNodes() {
        node = new Node[gp.maxWorldCol][gp.maxWorldRow];

        int col = 0;
        int row = 0;

        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {

            node[col][row] = new Node(col, row);

            col++;

            if (col == gp.maxWorldCol) {
                col = 0;
                row++;

            }
        }
    }

    public void resetNodes() {

        int col = 0;
        int row = 0;

        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {

            node[col][row].open = false;
            node[col][row].checked = false;
            node[col][row].solid = false;

            col++;

            if (col == gp.maxWorldCol) {
                col = 0;
                row++;

            }

            openList.clear();
            pathList.clear();
            goalReached = false;
            step = 0;

        }
    }

    public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {

        resetNodes();

        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];
        openList.add(currentNode);

        int col = 0;
        int row = 0;

        while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
            // Set solid node
            // Check tiles
            int tileNum = gp.tileM.maxTileNum[gp.currentMap][col][row];

            if (gp.tileM.tile[tileNum].collision == true) {
                node[col][row].solid = true;
            }
            // check interactive tiles
            for (int i = 0; i < gp.iTile[1].length; i++) {

                if (gp.iTile[gp.currentMap][i] != null && gp.iTile[gp.currentMap][i].destructible == true) {
                    int itCol = gp.iTile[gp.currentMap][i] / gp.maxWorldRow / gp.tileSize;
                    int itRow = gp.iTile[gp.currentMap][i] / gp.maxWorldCol / gp.tileSize;
                    node[itCol][itRow].solid = true;

                }
            }
            // set cost
            getCost(node[col][row]);
            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    public void getCost(Node node) {
        // G cost
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        // H cost
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;
        // F cost
        node.fCost = node.gCost + node.hCost;
    }

    public boolean search() {
        while (goalReached == false && step < 500) {
            int col = currentNode.col;
            int row = currentNode.row;
            // check current node
            currentNode.checked = true;
            openList.remove(currentNode);
            // open up node
            if (row - 1 >= 0) {
                openNode(node[col][row - 1]);
            }
            // left node
            if (col - 1 >= 0) {
                openNode(node[col - 1][row]);
            }
            // down node
            if (row + 1 < gp.maxWorldRow) {
                openNode(node[col][row + 1]);
            }
            // right node
            if (col + 1 < gp.maxWorldCol) {
                openNode(node[col + 1][row]);
            }

            // find best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for (int i = 0; i < openList.size(); i++) {
                // check is f cost is better
                if (openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                } else if (openList.get(i).fCost == bestNodefCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }
            // if no node in openList, then end the loop
            if (openList.size() == 0) {
                break;
            }

            // After the loop, openList[bestNodeIndex] is the next step (= currentNode)
            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }

            step++;
        }

        return goalReached;
    }

    public void openNode(Node node) {
        if (node.open == false && node.checked == false && node.solid == false) {
            node.open = true;
            node.parent = currentNode;
            openList.add(node);

        }
    }

    public void trackThePath() {

        Node current = goalNode;

        while (current != startNode) {

            // With this santa can track the path
            pathList.add(0, current);
            current = current.parent;
        }
    }
}