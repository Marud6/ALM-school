#include "linked_list_template.h"
#include <stdio.h>
#include <regex>

struct Post {
    string text;
    string date;
    Post(string t, string d) : text(t), date(d) {}
};

string readDate() {
    string date;
    regex datePattern(R"((0?[1-9]|[12][0-9]|3[01])\.(0?[1-9]|1[0-2])\.\d{4})");
    while (true) {
        cout << "Datum (DD.MM.YYYY): ";
        cin >> date;
        if (regex_match(date, datePattern)) {
            break;
        } else {
            cout << "Neplatný formát datumu, zkuste znovu.\n";
        }
    }
    return date;
}

string readMultiLineText() {
    string text, line;
    while (true) {
        getline(cin, line);
        if (line == "uloz") break;
        if (!text.empty()) text += "\n";
        text += line;
    }
    return text;
}

bool approve(){
    string nextOperation;
    while(nextOperation !="zavri"){
        cout<<"potvrďte prikazem ano nebo zavrete prikazem ne \n";
        cin>>nextOperation;
        if (nextOperation == "ano") {
            return true;
        } else {
            cout << "Invalid operation\n";
        }
    }
    return false;

}


int main(){
    single_linked_list<Post> LinkedList;
    single_linked_list<Post>::Node* current= nullptr;
    int postCount=0;
    string operation="";
    while(true){
       cout << "\033[2J\033[1;1H";
        cout <<
    "---------------------------------------------------------------------\n"
    "• predchozi - ukáže předchozí záznam\n"
    "• dalsi - ukáže následující záznam\n"
    "• zacatek - přenese mě na první záznam\n"
    "• konec - přenese mě na poslední záznam\n"
    "• novy - umožní přidat nový záznam za aktuálně zobrazený záznam\n"
    "• uloz - uloží nově vytvořený záznam\n"
    "• smaz - odstraní zobrazovaný záznam\n"
    "• zavri - ukončí program\n"
    "---------------------------------------------------------------------\n";
      cout <<"počet záznamů "+to_string(postCount)+"\n";
       if(current!=nullptr){
           cout<<"Datum: ";
           cout<<current->data.date<<"\n";
          cout<<"Text:\n";
          cout<<current->data.text<<"\n";
       }
      cout << "---------------------------------------------------------------------\n";
      cin>>operation;

        if (operation == "predchozi") {
          if(current==nullptr || current->prev==nullptr ){
            continue;
          }
          current=current->prev;

        } else if (operation == "dalsi") {
            if(current==nullptr || current->next==nullptr ){
                continue;
            }
            current=current->next;
        } else if (operation == "zacatek") {
          current=LinkedList.first();

        } else if (operation == "konec") {
            current=LinkedList.last();
        } else if (operation == "novy") {
          string date,text,nextOperation;
          date = readDate();
          text=readMultiLineText();
            if (current == nullptr) {
                current = LinkedList.addFirst(Post(text, date));
            } else {
                current = LinkedList.addAfter(current, Post(text, date));
            }
            postCount++;
        }
       else if (operation == "smaz") {
          if(approve()){
              current=LinkedList.deleteNode(current);
              postCount--;
          }
        } else if (operation == "zavri") {
          break;
        } else {
           cout << operation << " was not found\n";
        }
    }
    return 0;
}
