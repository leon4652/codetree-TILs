import java.io.*;
import java.util.*;
public class Main {
   static int N, M, K, res;
   static int[] DR = {0, -1, 0, 1}; //우 상 좌 하
   static int[] DC = {1, 0, -1, 0};
   static Pos[] startBallPos;
   static Field[][] map;
   static Queue<Pos> bfs = new ArrayDeque<>();
   static Queue<Pos[]> temp = new ArrayDeque<>();
   static boolean[][] check;

   
   static void log(String msg) {
      System.out.println(msg + " -------------");
      for(int i = 0; i < N; i++) {
         for(int j = 0; j < N; j++) {
            Field f = map[i][j];
            if(f.isHead) System.out.print("H");
            else if(f.isPeople) System.out.print("P");
            else if(f.isTail) System.out.print("T");
            else if(f.isLine) System.out.print("*");
            else System.out.print("-");
            System.out.print(" ");
         }
         System.out.println();
      }
   }
   
   static void solve() {
      for(int i = 0; i < K; i++) { //라운드
//         log("start");
         move();
         throwBall(i);
//         log("end");
      }
      System.out.println(res);
   }
   
   
   static void throwBall(int round) {
      int type = (round % (4*N)) / N; //DR 안에 들어갈 거
      int depth = (round % (4*N)) % N;
//      System.out.println("라운드 : " + round + "타입 " + type);
    		  
      Pos info = startBallPos[type];
      int d = info.d; //depth랑 연계
      int r = info.r;
      int c = info.c;
      
      //시작 지점
      int nr = r + (depth * DR[d]);
      int nc = c + (depth * DC[d]);
   
      for(int i = 0; i < N; i++) {
    	  int resR = nr + (DR[type] * i);
    	  int resC = nc + (DC[type] * i);
//    	  System.out.println("- 체크 좌표 " + resR + ", " + resC);
    	  if(map[resR][resC].isPeople || map[resR][resC].isHead || map[resR][resC].isTail) {
    		  scoringAndShift(resR, resC); //점수 계산 및 방향 바꾸기
    		  return;
    	  }
      }
   }
   static void scoringAndShift(int r, int c) {
	   Pos head = null;
	   Pos tail = null;
	   bfs.clear();
	   bfs.add(new Pos(r, c));
	   check = new boolean[N][N];
	   while(!bfs.isEmpty()) {
		   Pos p = bfs.poll();
		   if(check[p.r][p.c]) continue;
		   check[p.r][p.c] = true;
		   if(map[p.r][p.c].isHead) head = p;
		   if(map[p.r][p.c].isTail) tail = p;
		   if(head != null && tail != null) break;
		   
		   for(int i = 0; i < 4; i++) {
			   int nr = p.r + DR[i];
			   int nc = p.c + DC[i];
			   if(cantGo(nr, nc) || check[nr][nc]) continue;
			   bfs.add(new Pos(nr, nc));
		   }
	   }
	   if(head == null || tail == null) new RuntimeException("fail to find head or tail at scoring");
	   //현재 위치에서 머리 까지의 길이 탐색
	   int len = Math.abs(head.r - r) + Math.abs(head.c - c);
	   int score = (len + 1) * (len + 1);
	   res += score;
	   
	   //꼬리 머리 변환
	   map[head.r][head.c].isHead = false;
	   map[head.r][head.c].isTail = true;
	   map[tail.r][tail.c].isHead = true;
	   map[tail.r][tail.c].isTail = false;
   }
		

   static void move() {
      check = new boolean[N][N];
      for(int i = 0; i < N; i++) {
         for(int j = 0; j < N; j++) {
            if(map[i][j].isHead) moveEach(new Pos(i, j));
         }
      }
      
      setPos();
   }
   
   static void moveEach(Pos head) {
      Pos nextHead = new Pos(-1, -1); //머리 이동 예정 위치
      Pos nextTail = new Pos(-1, -1);
      
      //머리 이동 예정
      for(int i = 0; i < 4; i++) {
         int nr = head.r + DR[i];
         int nc = head.c + DC[i];
         if(cantGo(nr, nc)) continue; //갈 수 없음
         if(map[nr][nc].isLine && !map[nr][nc].isPeople && !map[nr][nc].isTail) { 
            //갈 수 있는 길이며, 사람이 아니어야 함
            nextHead.r = nr;
            nextHead.c = nc;
            break;
         }
      }
      
      //꼬리 찾기
      Pos tail = findTail(head.r, head.c);

      temp.add(new Pos[] {head, tail, nextHead, nextTail});
   }
   
   
   static void setPos() {
      while(!temp.isEmpty()) {
         Pos[] now = temp.poll();
         Pos head = now[0];
         Pos tail = now[1];
         Pos nextHead = now[2];
         Pos nextTail = now[3];
         
         
         //꼬리 이동 위치
         for(int i = 0; i < 4; i++) {
            int nr = tail.r + DR[i];
            int nc = tail.c + DC[i];
            if(cantGo(nr, nc)) continue; //갈 수 없음
            if(map[nr][nc].isLine && (map[nr][nc].isPeople || map[nr][nc].isHead)) { 
               //갈 수 있는 길이며, 앞사람이 있었어야 함
               nextTail.r = nr;
               nextTail.c = nc;
               break;
            }
         }
         
         //이동하면서 현재 머리 꼬리 위치 지우기
         //머리 기존 값 지우기
         map[head.r][head.c].isHead = false;
         map[head.r][head.c].isPeople = true; //뒷사람 이동
         map[nextHead.r][nextHead.c].isHead = true;
         map[nextHead.r][nextHead.c].isPeople = false;
         //꼬리 기존 값 지우기
         map[tail.r][tail.c].isTail = false;
         map[nextTail.r][nextTail.c].isTail = true;
         map[nextTail.r][nextTail.c].isPeople = false; //여기는 이제 꼬리 자리임
      }
   }
   
   static Pos findTail(int r, int c) {
      bfs.clear();
      bfs.add(new Pos(r, c));
      while(!bfs.isEmpty()) {
         Pos p = bfs.poll();
         if(map[p.r][p.c].isTail) {
            return p;
         }
         check[p.r][p.c] = true;
         for(int i = 0; i < 4; i++) {
            int nr = p.r + DR[i];
            int nc = p.c + DC[i];
            if(cantGo(nr, nc) || check[nr][nc] || !map[nr][nc].isLine) continue;
            if(map[nr][nc].isPeople || map[nr][nc].isTail) {
               bfs.add(new Pos(nr, nc));               
            }
         }
      }
      
      throw new RuntimeException("findTail fail");
   }
   
   
   public static boolean cantGo(int r, int c) {
      return r < 0 || c < 0 || r >= N || c >= N;
   }
   
   public static void main(String[] args) throws Exception {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      StringTokenizer st = new StringTokenizer(br.readLine());
      N = Integer.parseInt(st.nextToken());
      M = Integer.parseInt(st.nextToken());
      K = Integer.parseInt(st.nextToken());
      //라운드별 공 시작 위치
      startBallPos = new Pos[4];
      startBallPos[0] = new Pos(0, 0, 3); //초기 위치(r, c) 및 전부 순회 시 다음 방향 d
      startBallPos[1] = new Pos(N - 1, 0, 0);
      startBallPos[2] = new Pos(N - 1, N - 1, 1);
      startBallPos[3] = new Pos(0, N - 1, 2);
      
      //필드 초기화
      map = new Field[N][N];
      for(int i = 0; i < N; i++) {
         st = new StringTokenizer(br.readLine());
         for(int j = 0; j < N; j++) {
            int val = Integer.parseInt(st.nextToken());
            map[i][j] = new Field(i, j, val);
         }
      }
      
      solve();
   }
}

class Pos {
   int r, c , d;
   public Pos(int r, int c) {
      this.r = r;
      this.c = c;
   }
   public Pos(int r, int c, int d) {
      this.r = r;
      this.c = c;
      this.d = d;
   }
   
   
}

class Field {
   Pos p;
   boolean isLine; // 4 이동선
   boolean isHead;   // 1 머리
   boolean isPeople; // 2 사람 있는지 : 꼬리나 머리는 해당 안됨
   boolean isTail; // 3 꼬리
   
   public Field(int r, int c, int val) {
      this.p = new Pos(r, c);
      if(val != 0) this.isLine = true;
      if(val == 3) this.isTail = true;
      else if(val == 2) this.isPeople = true;
      else if(val == 1) this.isHead = true;
   }
}