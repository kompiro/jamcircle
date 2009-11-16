require 'jruby';
require 'irb';
require 'irb/completion';
Card = org.kompiro.jamcircle.kanban.model.mock.Card; 
Lane = org.kompiro.jamcircle.kanban.model.mock.Lane; 
User = org.kompiro.jamcircle.kanban.model.mock.User;
def board
  return $board_accessor.board
end

def create_card(subject=nil)
  card = Card.new
  card.subject = subject
  board.add_card card
  return card
end

def create_lane(status=nil)
  lane = Lane.new
  lane.status = status
  board.add_lane lane
  return lane
end

IRB.setup nil;
irb = IRB::Irb.new;
irb.context.prompt_mode=:DEFAULT;
IRB.conf[:IRB_RC].call(irb.context) if IRB.conf[:IRB_RC];
IRB.conf[:MAIN_CONTEXT] = irb.context;
trap("SIGINT") do
  irb.signal_handle;
end;
catch(:IRB_EXIT) do
  irb.eval_input;
end
