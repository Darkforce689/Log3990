// TODO GL3A22107-5 : tests -> to be removed / converted to be sent to server
// describe('PlaceLetter', () => {
//     let timer: TimerService;
//     const lettersToPlace = 'bateau';
//     const placement: PlacementSetting = {
//         x: 0,
//         y: 0,
//         direction: Direction.Horizontal,
//     };
//     let game: OfflineGame;
//     let specialGame: SpecialOfflineGame;
//     const player1: Player = new User('Tim');
//     const player2: Player = new User('George');
//     let placeLetter: PlaceLetter;
//     let activePlayer: Player;
//     let letterCreator: LetterCreator;
//     let pointCalculatorSpy: PointCalculatorService;
//     let objectiveCreatorSpy: jasmine.SpyObj<ObjectiveCreator>;
//     const randomBonus = false;
//     beforeEach(() => {
//         timer = new TimerService();
//         pointCalculatorSpy = jasmine.createSpyObj('PointCalculatorService', ['placeLetterCalculation']);
//         pointCalculatorSpy.placeLetterCalculation = jasmine.createSpy().and.callFake((action, listOfWord) => {
//             const points = action.word.length + listOfWord.length;
//             const player = action.player;
//             player.points = points;
//             return points;
//         });

//         objectiveCreatorSpy = jasmine.createSpyObj(ObjectiveCreator, ['updateObjectives']);
//         TestBed.configureTestingModule({
//             providers: [
//                 { provide: PointCalculatorService, useValue: pointCalculatorSpy },
//                 { provide: ObjectiveCreator, useValue: objectiveCreatorSpy },
//             ],
//         });
//         const boardService = TestBed.inject(BoardService);
//         const messages = TestBed.inject(MessagesService);
//         game = new OfflineGame(randomBonus, DEFAULT_TIME_PER_TURN, timer, pointCalculatorSpy, boardService, messages);
//         specialGame = new SpecialOfflineGame(
//             randomBonus,
//             DEFAULT_TIME_PER_TURN,
//             timer,
//             pointCalculatorSpy,
//             boardService,
//             messages,
//             objectiveCreatorSpy,
//         );
//         game.players.push(player1);
//         game.players.push(player2);
//         game.start();
//         letterCreator = new LetterCreator();
//         const letterObjects = letterCreator.createLetters(lettersToPlace.split(''));
//         activePlayer = game.getActivePlayer();
//         for (let i = 0; i < letterObjects.length; i++) {
//             activePlayer.letterRack[i] = letterObjects[i];
//         }
//         placeLetter = new PlaceLetter(activePlayer, lettersToPlace, placement);
//     });

//     it('should create an instance', () => {
//         activePlayer = game.getActivePlayer();
//         expect(new PlaceLetter(activePlayer, lettersToPlace, placement)).toBeTruthy();
//     });

//     it('should place letter at right place', () => {
//         placeLetter.execute(game);
//         for (let i = 0; i < lettersToPlace.length; i++) {
//             expect(game.board.grid[0][i].letterObject.char).toBe(lettersToPlace.charAt(i).toUpperCase());
//         }
//     });

//     it('should have proper revert behavior', fakeAsync(() => {
//         const TIME_BEFORE_REVERT = 3000;
//         placeLetter.execute(game);
//         tick(TIME_BEFORE_REVERT);
//         for (let i = 0; i < lettersToPlace.length; i++) {
//             expect(game.board.grid[i][0].letterObject.char).toBe(' ');
//         }
//     }));

//     it('should add points when action valid', () => {
//         const LIST_OF_WORD_LENGTH = 1;
//         const points = placeLetter.word.length + LIST_OF_WORD_LENGTH;
//         placeLetter.execute(game);
//         activePlayer = game.getActivePlayer();
//         expect(activePlayer.points).toBe(points);
//     });

//     it('#isCharUpperCase should throw error', () => {
//         const notChar = 'AB';
//         expect(() => {
//             isCharUpperCase(notChar);
//         }).toThrowError();
//     });

//     it('should place letters in vertical', () => {
//         const newPlacement = { ...placement };
//         newPlacement.direction = Direction.Vertical;
//         placeLetter = new PlaceLetter(activePlayer, lettersToPlace, newPlacement);
//         placeLetter.execute(game);

//         const word = placeLetter.word;
//         for (let y = 0; y < word.length; y++) {
//             expect(game.board.grid[y][0].letterObject.char).toBe(word.charAt(y).toUpperCase());
//         }
//     });

//     it('should place blank letter', () => {
//         activePlayer.letterRack[0] = letterCreator.createLetter('*');
//         const wordToPlace = 'Bateau';
//         placeLetter = new PlaceLetter(activePlayer, wordToPlace, placement);
//         placeLetter.execute(game);

//         const word = placeLetter.word;
//         for (let x = 0; x < word.length; x++) {
//             expect(game.board.grid[0][x].letterObject.char).toBe(word.charAt(x).toUpperCase());
//         }
//     });

//     it('should place letter at right place with letters on board', () => {
//         game.board.grid[0][0].letterObject = letterCreator.createLetter('B');
//         placeLetter.execute(game);
//         for (let i = 0; i < lettersToPlace.length; i++) {
//             expect(game.board.grid[0][i].letterObject.char).toBe(lettersToPlace.charAt(i).toUpperCase());
//         }
//     });

//     it('should call update objective if the game the action is performing on is special', () => {
//         const spy = spyOn(specialGame, 'updateObjectives');
//         placeLetter.execute(specialGame);
//         expect(spy).toHaveBeenCalled();
//     });
// });
